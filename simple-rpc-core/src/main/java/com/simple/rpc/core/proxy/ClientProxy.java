package com.simple.rpc.core.proxy;

import com.simple.rpc.core.transport.Client;

/**
 * @author ssk www.hnapay.com Inc.All rights reserved
 * @version v1.0
 * @date 2018-04-18-上午 9:10
 */
public interface ClientProxy {

    public <T> T proxyInterface(Client client, final Class<T> serviceInterface);
}
