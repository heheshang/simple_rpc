package com.simple.rpc.core.transport;

import lombok.Data;

/**
 * 响应
 * @author ssk www.hnapay.com Inc.All rights reserved
 * @version v1.0
 * @date 2018-04-17-下午 4:36
 */
@Data
public class Response {
    private long requestId;
    private Object response;
    private Throwable throwable;
}
