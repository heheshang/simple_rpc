package com.simple.rpc.core.bootstrap;

import com.google.common.base.Preconditions;
import com.simple.rpc.core.proxy.CglibClientProxy;
import com.simple.rpc.core.proxy.ClientProxy;
import com.simple.rpc.core.transport.ClientImpl;

public class ClientBuilder<T> {
    private String serviceName;
    private String zkConn;
    private Class<T> serviceInterface;
    private int requestTimeoutMillis = 10000;
    private Class<? extends ClientProxy> clientProxyClass = CglibClientProxy.class;

    public static <T> ClientBuilder<T> builder() {
        return new ClientBuilder<T>();
    }

    public ClientBuilder<T> serviceName(String serviceName) {
        this.serviceName = serviceName;
        return this;
    }

    public ClientBuilder<T> zkConn(String zkConn) {
        this.zkConn = zkConn;
        return this;
    }

    public ClientBuilder<T> serviceInterface(Class<T> serviceInterface) {
        this.serviceInterface = serviceInterface;
        return this;
    }

    public ClientBuilder<T> requestTimeout(int requestTimeoutMillis) {
        this.requestTimeoutMillis = requestTimeoutMillis;
        return this;
    }

    public ClientBuilder<T> clientProxyClass(Class<? extends ClientProxy> clientProxyClass) {
        this.clientProxyClass = clientProxyClass;
        return this;
    }

    public T build() {
        Preconditions.checkNotNull(serviceInterface);
        Preconditions.checkNotNull(zkConn);
        Preconditions.checkNotNull(serviceName);
        ClientImpl client = new ClientImpl(serviceName);
        client.setZkConn(zkConn);
        client.setClientProxyClass(clientProxyClass);
        client.setRequestTimeoutMills(requestTimeoutMillis);
        client.init();
        return client.proxyInterface(serviceInterface);
    }
}
