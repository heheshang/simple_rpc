package com.simple.rpc.serializer;

/**
 * 序列化接口
 * @author ssk www.hnapay.com Inc.All rights reserved
 * @version v1.0
 * @date 2018-04-17-下午 4:16
 */
public interface Serializer {

    public byte[] serialize(Object obj);

    public <T> T deserialize(byte[] bytes);
}
