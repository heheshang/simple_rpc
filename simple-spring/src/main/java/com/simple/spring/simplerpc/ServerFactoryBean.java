package com.simple.spring.simplerpc;


import com.simple.rpc.core.bootstrap.ServerBuilder;
import com.simple.rpc.core.transport.Server;
import com.simple.rpc.core.transport.ServerImpl;
import org.springframework.beans.factory.FactoryBean;

/**
 * @author ssk www.hnapay.com Inc.All rights reserved
 * @version v1.0
 * @date 2018-04-27  下午 3:35
 */

public class ServerFactoryBean implements FactoryBean<Object> {

    // 远程调用的接口
    private Class<?> serviceInterface;

    private Object serviceImpl;

    private String ip;

    private int port;

    private String serviceName;

    private String zkConn;

    private ServerImpl rpcServer;

    @Override
    public Object getObject() throws Exception {

        return this;
    }

    public void start() {

        Server build = ServerBuilder
                .builder()
                .serviceImpl(serviceImpl)
                .serviceName(serviceName)
                .port(port)
                .zkConn(zkConn).build();

        System.out.println("构建server 开始");
        build.start();
        rpcServer = new ServerImpl(port, serviceImpl, serviceName);
        rpcServer.setZkConn(getZkConn());
        rpcServer.start();
    }

    public void destroy() {

        rpcServer.shutdown();
    }

    @Override
    public Class<?> getObjectType() {

        return this.getClass();
    }

    @Override
    public boolean isSingleton() {

        return true;
    }


    public Class<?> getServiceInterface() {

        return serviceInterface;
    }


    public String getIp() {

        return ip;
    }

    public void setIp(String ip) {

        this.ip = ip;
    }

    public int getPort() {

        return port;
    }

    public void setPort(int port) {

        this.port = port;
    }

    public Object getServiceImpl() {

        return serviceImpl;
    }

    public void setServiceInterface(Class<?> serviceInterface) {

        this.serviceInterface = serviceInterface;
    }

    public void setServiceImpl(Object serviceImpl) {

        this.serviceImpl = serviceImpl;
    }

    public String getServiceName() {

        return serviceName;
    }

    public void setServiceName(String serviceName) {

        this.serviceName = serviceName;
    }

    public ServerImpl getRpcServer() {

        return rpcServer;
    }

    public void setRpcServer(ServerImpl rpcServer) {

        this.rpcServer = rpcServer;
    }

    public String getZkConn() {

        return zkConn;
    }

    public void setZkConn(String zkConn) {

        this.zkConn = zkConn;
    }


}
