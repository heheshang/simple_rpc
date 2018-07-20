package com.simple.rpc.exception;

/**
 * @author ssk www.hnapay.com Inc.All rights reserved
 * @version v1.0
 * @date 2018-04-18-上午 10:12
 */
public class SimpleException extends RuntimeException {
    public SimpleException() {
    }

    public SimpleException(String message) {
        super(message);
    }

    public SimpleException(Throwable cause) {
        super(cause);
    }
}
