package com.simple.rpc.core.handler;

import com.simple.rpc.core.codec.ProtocolDecoder;
import com.simple.rpc.core.codec.ProtocolEncoder;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * 客户端Channel初始化
 *
 * @author ssk www.hnapay.com Inc.All rights reserved
 * @version v1.0
 * @date 2018-04-17-下午 5:03
 */
public class ClientChannelInitializer extends ChannelInitializer<Channel> {

    @Override
    protected void initChannel(Channel channel) throws Exception {
        channel.pipeline().addLast(new LoggingHandler(LogLevel.INFO))
                .addLast(new ProtocolDecoder(10*1024*1024))
                .addLast(new ProtocolEncoder())
                .addLast(new RpcClientHandler());
    }
}
