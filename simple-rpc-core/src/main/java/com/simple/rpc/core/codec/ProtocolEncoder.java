package com.simple.rpc.core.codec;

import com.simple.rpc.serializer.KryoSerializer;
import com.simple.rpc.serializer.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * 编码器
 *
 * @author ssk www.hnapay.com Inc.All rights reserved
 * @version v1.0
 * @date 2018-04-17-下午 4:25
 */
public class ProtocolEncoder extends MessageToByteEncoder<Object> {


    private Serializer serializer = new KryoSerializer();

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object o, ByteBuf byteBuf) throws Exception {

        byte[] serializedBytes = serializer.serialize(o);

        int length = serializedBytes.length;

        byteBuf.writeInt(length);

        byteBuf.writeBytes(serializedBytes);

    }
}
