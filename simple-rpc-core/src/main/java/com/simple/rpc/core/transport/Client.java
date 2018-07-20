package com.simple.rpc.core.transport;

import java.lang.reflect.Method;

/**
 * 客户端
 *
 * @author ssk www.hnapay.com Inc.All rights reserved
 * @version v1.0
 * @date 2018-04-17-下午 4:38
 */
public abstract class Client {

    public abstract void close();

    public abstract Response sendMessage(Class<?> clazz, Method method, Object[] args);

    public abstract <T> T proxyInterface(Class<T> serviceInterface);
}
