package com.simple.rpc.core.codec;

import com.simple.rpc.serializer.KryoSerializer;
import com.simple.rpc.serializer.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 解码器
 *
 * @author ssk www.hnapay.com Inc.All rights reserved
 * @version v1.0
 * @date 2018-04-17-下午 4:08
 */
public class ProtocolDecoder extends LengthFieldBasedFrameDecoder {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProtocolDecoder.class);

    private static final int MSG_PROTOCOL_HEADER_FIELD_LENGTH = 4;


    private Serializer serializer = new KryoSerializer();

    public ProtocolDecoder(int maxFrameLength) {

        super(maxFrameLength, 0, 4, 0, 4);
    }


    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {

        ByteBuf decode = (ByteBuf) super.decode(ctx, in);

        if (decode != null) {

            int byteLength = decode.readableBytes();

            byte[] byteHolder = new byte[byteLength];

            decode.readBytes(byteHolder);

            return serializer.deserialize(byteHolder);
        }

        LOGGER.debug("Decoder Result is null");

        return null;
    }
}
