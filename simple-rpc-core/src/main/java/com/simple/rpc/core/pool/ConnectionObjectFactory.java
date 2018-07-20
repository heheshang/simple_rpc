package com.simple.rpc.core.pool;

import com.simple.rpc.core.handler.ClientChannelInitializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 连接工厂
 *
 * @author ssk www.hnapay.com Inc.All rights reserved
 * @version v1.0
 * @date 2018-04-17-下午 4:56
 */
public class ConnectionObjectFactory extends BasePooledObjectFactory<Channel> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConnectionObjectFactory.class);

    private String ip;

    private int port;

    public ConnectionObjectFactory(String ip, int port) {

        this.ip = ip;

        this.port = port;
    }

    @Override
    public Channel create() throws Exception {
        // 重试3次

        for (int i = 0; i < 3; i++) {

            Channel channel = connectNewChannel();

            if (channel != null) {

                return channel;

            }
        }

        return null;
    }

    private Channel connectNewChannel() {

        Bootstrap bootstrap = new Bootstrap();

        bootstrap.channel(NioSocketChannel.class)
                .group(new NioEventLoopGroup(1))
                .handler(new ClientChannelInitializer());

        try {


            final ChannelFuture f = bootstrap
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .connect(ip, port).sync();

            final Channel channel = f.channel();

            addChannelListeners(f, channel);

            return channel;

        } catch (InterruptedException e) {
            LOGGER.error("Interrupted " + e);
            Thread.currentThread().isInterrupted();
        }

        return null;
    }

    /**
     * 添加监听器
     *
     * @param f
     * @param channel
     */
    private void addChannelListeners(ChannelFuture f, Channel channel) {

        f.addListener(new ChannelFutureListener() {

            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {

                if (channelFuture.isSuccess()) {
                    LOGGER.info("Connect success {} ", f);
                }
            }

        });

        channel.closeFuture().addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {

                LOGGER.info("Channel Close {} {}", ip, port);
            }
        });

    }


    @Override
    public boolean validateObject(PooledObject<Channel> p) {

        Channel object = p.getObject();

        return object.isActive();

    }

    @Override
    public PooledObject<Channel> wrap(Channel channel) {

        return new DefaultPooledObject<Channel>(channel);
    }
}
