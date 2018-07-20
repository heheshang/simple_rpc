package com.simple.rpc.core;

import com.simple.rpc.serializer.KryoSerializer;
import org.junit.Test;

import java.util.Date;

/**
 * Description:
 *
 * @author liuzhengyang
 * @version 1.0
 * @since 2016-12-17
 */
public class KryoSerializerTest {

    @Test
    public void serialize() throws Exception {

        KryoSerializer serializer = new KryoSerializer();
        Person p = new Person();
        p.setAge(10);
        p.setCreateTime(124124);
        p.setName("nihao");
        p.setUpdated(new Date());
        p.setClazz(String.class);
        byte[] serialize = serializer.serialize(p);

        System.out.println(serialize.length);

        Person deserialize = serializer.deserialize(serialize);
        System.out.println(deserialize);
    }


}