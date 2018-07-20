package com.simple.rpc.core.transport;

import com.simple.rpc.core.codec.ProtocolDecoder;
import com.simple.rpc.core.codec.ProtocolEncoder;
import com.simple.rpc.core.handler.RpcServerHandler;
import com.simple.rpc.core.utils.InetUtil;
import com.simple.rpc.core.utils.RegistryUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author ssk www.hnapay.com Inc.All rights reserved
 * @version v1.0
 * @date 2018-04-17-下午 4:44
 */
public class ServerImpl extends Server {

    private static final Logger logger = LoggerFactory.getLogger(ServerImpl.class);

    /**
     * ip
     */
    private String ip;

    /**
     * 端口
     */
    private int port;

    /**
     * 启动标识
     */
    private volatile boolean strated = false;

    /**
     *
     */
    private Channel channel;

    /**
     * 实现服务
     */
    private Object serviceImpl;

    /**
     * 服务名称
     */
    private String serviceName;

    /**
     * zk连接
     */
    private String zkConn;

    /**
     * 服务注册地址
     */
    private String serviceRegisterPath;


    private EventLoopGroup bossGroup = new NioEventLoopGroup();

    private EventLoopGroup workerGroup = new NioEventLoopGroup();

    private CuratorFramework curatorFramework;

    public ServerImpl(int port, Object serviceImpl, String serviceName) {

        this.port = port;
        this.serviceImpl = serviceImpl;
        this.serviceName = serviceName;
    }

    public ServerImpl(int port, Object serviceImpl, String serviceName, String zkConn) {

        this.port = port;
        this.serviceImpl = serviceImpl;
        this.serviceName = serviceName;
        this.zkConn = zkConn;
    }


    public String getIp() {

        return ip;
    }

    public void setIp(String ip) {

        this.ip = ip;
    }

    public Channel getChannel() {

        return channel;
    }

    public void setChannel(Channel channel) {

        this.channel = channel;
    }

    public String getZkConn() {

        return zkConn;
    }

    public void setZkConn(String zkConn) {

        this.zkConn = zkConn;
    }

    @Override
    public void start() {
        ServerBootstrap bootstrap = new ServerBootstrap();

        bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 100)
                .handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {

                        ch.pipeline().addLast(new LoggingHandler(LogLevel.INFO))
                                .addLast(new ProtocolDecoder(10 * 1024 * 1024))
                                .addLast(new ProtocolEncoder())
                                .addLast(new RpcServerHandler(serviceImpl));
                    }
                });


        try {

            ChannelFuture sync = bootstrap.bind(port).sync();

            registerService();

            logger.info("Service Started at port {}", port);

            strated = true;

            this.channel = sync.channel();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void registerService() {

        String zkConn = getZkConn();

        ip = InetUtil.getLocalIp();

        String ipPortStr = ip + ":" + port;

        curatorFramework = CuratorFrameworkFactory.newClient(zkConn, new ExponentialBackoffRetry(1000, 1));

        curatorFramework.start();

        String serviceBasePath = RegistryUtil.getServicePath(serviceName);

        try {

            curatorFramework.create().creatingParentsIfNeeded().forPath(serviceBasePath);


        } catch (Exception e) {
            if (e.getMessage().contains("NodeExist")) {
                logger.error("path already Exist");
            } else {
                logger.error("Create Path Error {}", e);
                throw new RuntimeException("Register error");
            }
        }

        boolean registerSuccess = false;

        while (!registerSuccess){

            try {

                curatorFramework.create().withMode(CreateMode.EPHEMERAL).forPath(serviceBasePath+"/"+ipPortStr);

                registerSuccess = true;

            } catch (Exception e) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }

                logger.info("Retry Register ZK, {} ",e.getMessage());

                try {
                    curatorFramework.delete().forPath(serviceBasePath+"/"+ipPortStr);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }

        }
    }

    @Override
    public void shutdown() {

        logger.info("Shutting down servre {} ",serviceName);

        unRegister();

        if (curatorFramework!=null){

            curatorFramework.close();
        }

        strated =false;

        bossGroup.shutdownGracefully();

        workerGroup.shutdownGracefully();
    }

    private void unRegister() {

        logger.info("unRegister zookeeper");

        try {
            String serviceInstancePath = RegistryUtil.getServiceInstancePath(serviceName,ip,port);
            curatorFramework.delete().forPath(serviceInstancePath);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
