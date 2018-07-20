package com.simple.rpc.exception;

/**
 * @author ssk www.hnapay.com Inc.All rights reserved
 * @version v1.0
 * @date 2018-04-18-上午 9:51
 */
public class RequestTimeoutException extends SimpleException {

    public RequestTimeoutException(String s) {
        super(s);
    }
}
