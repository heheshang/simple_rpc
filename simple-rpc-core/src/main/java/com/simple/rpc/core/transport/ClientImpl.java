package com.simple.rpc.core.transport;

import com.google.common.base.Splitter;
import com.simple.rpc.core.proxy.ClientProxy;
import com.simple.rpc.core.proxy.JdkClientProxy;
import com.simple.rpc.core.utils.RegistryUtil;
import com.simple.rpc.exception.RequestTimeoutException;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.GetChildrenBuilder;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 客户端实现
 *
 * @author ssk www.hnapay.com Inc.All rights reserved
 * @version v1.0
 * @date 2018-04-17-下午 4:39
 */
@Slf4j
public class ClientImpl extends Client {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientImpl.class);

    private static AtomicLong atomicLong = new AtomicLong();

    /**
     * 发布的服务名称,用来寻找对应的服务提供者
     */
    private String serviceName;

    private EventLoopGroup eventLoopGroup = new NioEventLoopGroup(2);

    private String zkConn;

    /**
     * 默认10秒钟
     */
    private int requestTimeoutMills = 10 * 1000;

    private CuratorFramework curatorFramework;

    private Class<? extends ClientProxy> clientProxyClass;

    private ClientProxy clientProxy;

    /**
     * 存放字符串Channel对应的map
     */
    public static CopyOnWriteArrayList<ChannelWrapper> channelWrappers = new CopyOnWriteArrayList<ChannelWrapper>();

    public ClientImpl(String serviceName) {

        this.serviceName = serviceName;
    }

    public void init() {


        // TODO 这段代码需要仔细检查重构整理
        // 注册中心不可用时,保存本地缓存
        // TODO 启动一个后台任务, 定期检查服务器列表g
        curatorFramework = CuratorFrameworkFactory.newClient(getZkConn(), new ExponentialBackoffRetry(1000, 3));
        curatorFramework.start();

        try {

            final GetChildrenBuilder children = curatorFramework.getChildren();

            final String serviceZkPath = RegistryUtil.getServicePath(serviceName);

            PathChildrenCache pathChildrenCache = new PathChildrenCache(curatorFramework, serviceZkPath, true);

            pathChildrenCache.start();

            pathChildrenCache.getListenable().addListener(new PathChildrenCacheListener() {
                @Override
                public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {

                    LOGGER.info("Listen Event {}", event);

                    List<String> newServiceData = children.forPath(serviceZkPath);

                    LOGGER.info("Server {} list change {}", serviceName, newServiceData);

                    //关闭删除本地缓存中多出的channel
                    for (ChannelWrapper wrapper : channelWrappers) {

                        String connStr = wrapper.getConnStr();

                        if (!newServiceData.contains(connStr)) {

                            wrapper.close();

                            LOGGER.info("Remove channel {}", connStr);

                            channelWrappers.remove(wrapper);
                        }

                    }


                    for (String connStr : newServiceData) {

                        boolean containThis = false;

                        for (ChannelWrapper wrapper : channelWrappers) {

                            if (connStr != null && connStr.equals(wrapper.getConnStr())) {

                                containThis = true;
                            }
                        }

                        if (!containThis) {

                            addNewChannel(connStr);
                        }

                    }


                }
            });

            List<String> strings = children.forPath(serviceZkPath);

            if (CollectionUtils.isEmpty(strings)) {

                throw new RuntimeException("No Service available for " + serviceName);
            }

            LOGGER.info("Found Server {} List {}", serviceName, strings);

            for (String connStr : strings) {
                try {

                    addNewChannel(connStr);
                } catch (Exception e) {
                    LOGGER.error("Add New Channel Exception", e);
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addNewChannel(String connStr) {

        try {
            List<String> strings = Splitter.on(":").splitToList(connStr);
            strings.forEach(s -> log.info("打印链接信息value=[{}]", s));
            if (strings.size() != 2) {
                throw new RuntimeException("Error connection str " + connStr);
            }
            String host = strings.get(0);
            int port = Integer.parseInt(strings.get(1));
            ChannelWrapper channelWrapper = new ChannelWrapper(host, port);
            channelWrappers.add(channelWrapper);
            channelWrappers.forEach(wrapper -> log.info("打印包装器信息,wrapper[{}]", wrapper));
            LOGGER.info("Add New Channel {}, {}", connStr, channelWrapper);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void close() {

        if (curatorFramework != null) {
            curatorFramework.close();
        }
        try {
            for (ChannelWrapper cw : channelWrappers) {
                cw.close();
            }
        } finally {
            eventLoopGroup.shutdownGracefully();
        }
    }

    @Override
    public Response sendMessage(Class<?> clazz, Method method, Object[] args) {

        Request request = new Request();

        request.setRequestId(atomicLong.incrementAndGet());

        request.setMethod(method.getName());

        request.setParams(args);

        request.setClazz(clazz);

        request.setParameterTypes(method.getParameterTypes());

        log.info("打印请求request信息[{}]", request);

        ChannelWrapper channelWrapper = selectChannel();

        if (channelWrapper == null) {

            Response response = new Response();

            RuntimeException runtimeException = new RuntimeException("Channel is not active now");

            response.setThrowable(runtimeException);

            return response;
        }

        Channel channel = null;

        try {
            channel = channelWrapper.getChannelObjectPool().borrowObject();
            log.info("打印channel信息[{}]", channel);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (channel == null) {

            Response response = new Response();

            RuntimeException runtimeException = new RuntimeException("Channel is not available now");

            response.setThrowable(runtimeException);

            return response;
        }

        try {

            channel.writeAndFlush(request);

            BlockingQueue<Response> blockingQueue = new ArrayBlockingQueue<Response>(1);

            ResponseHolder.responseMap.put(request.getRequestId(), blockingQueue);

            Response response = blockingQueue.poll(requestTimeoutMills, TimeUnit.MILLISECONDS);

            if (response == null) {
                throw new RequestTimeoutException("service: " + serviceName + " metho:d " + method + " timeout exceed " + getRequestTimeoutMills());
            } else {
                return response;
            }

        } catch (InterruptedException e) {
            throw new RequestTimeoutException("service: " + serviceName + " metho:d " + method + " timeout exceed " + getRequestTimeoutMills());
        } finally {
            try {
                channelWrapper.getChannelObjectPool().returnObject(channel);
            } catch (Exception e) {
                e.printStackTrace();
            }
            ResponseHolder.responseMap.remove(request.getRequestId());
        }

    }

    private ChannelWrapper selectChannel() {

        Random random = new Random();

        int size = channelWrappers.size();

        if (size < 1) {
            return null;
        }

        int i = random.nextInt(size);

        return channelWrappers.get(i);
    }

    @Override
    public <T> T proxyInterface(Class<T> serviceInterface) {
        // default jdk proxy
//		clientProxy = new JdkClientProxy();
        if (clientProxyClass == null) {
            clientProxyClass = JdkClientProxy.class;
        }
        try {
            clientProxy = clientProxyClass.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return clientProxy.proxyInterface(this, serviceInterface);
    }


    public Class<? extends ClientProxy> getClientProxyClass() {

        return clientProxyClass;
    }

    public void setClientProxyClass(Class<? extends ClientProxy> clientProxyClass) {

        this.clientProxyClass = clientProxyClass;
    }

    public String getZkConn() {

        return zkConn;
    }

    public void setZkConn(String zkConn) {

        this.zkConn = zkConn;
    }

    public int getRequestTimeoutMills() {

        return requestTimeoutMills;
    }

    public void setRequestTimeoutMills(int requestTimeoutMills) {

        this.requestTimeoutMills = requestTimeoutMills;
    }
}
