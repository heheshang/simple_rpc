package com.simple.rpc.core.transport;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 执行器
 *
 * @author ssk www.hnapay.com Inc.All rights reserved
 * @version v1.0
 * @date 2018-04-17-下午 4:39
 */
public class ResponseHolder {

    public static ConcurrentMap<Long, BlockingQueue<Response>> responseMap = new ConcurrentHashMap<>();
}
