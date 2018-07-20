package com.simple.rpc.serializer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * kryo 序列接口实现
 *
 * @author ssk www.hnapay.com Inc.All rights reserved
 * @version v1.0
 * @date 2018-04-17-下午 4:18
 */
public class KryoSerializer implements Serializer {

    @Override
    public byte[] serialize(Object obj) {

        Kryo kryo = new Kryo();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        Output output = new Output(outputStream);

        kryo.writeClassAndObject(output, obj);

        output.close();

        return outputStream.toByteArray();
    }

    @Override
    public <T> T deserialize(byte[] bytes) {

        Kryo kryo = new Kryo();

        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);

        Input input = new Input(inputStream);

        input.close();

        return (T) kryo.readClassAndObject(input);
    }
}
