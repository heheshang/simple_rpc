package com.simple.rpc.core.handler;

import com.simple.rpc.core.transport.Request;
import com.simple.rpc.core.transport.Response;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

/**
 * 服务端
 *
 * @author ssk www.hnapay.com Inc.All rights reserved
 * @version v1.0
 * @date 2018-04-17-下午 5:21
 */
@Slf4j
public class RpcServerHandler extends SimpleChannelInboundHandler<Request> {

    private static final Logger LOGGER = LoggerFactory.getLogger(RpcServerHandler.class);

    private Object service;

    public RpcServerHandler(Object serviceImpl) {

        this.service = serviceImpl;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Request request) throws Exception {

        String methodName = request.getMethod();

        Object[] params = request.getParams();

        Class<?>[] paramTypes = request.getParameterTypes();

        long requestId = request.getRequestId();

        Method method = service.getClass().getDeclaredMethod(methodName, paramTypes);

        method.setAccessible(true);

        Object invoke = method.invoke(service, params);

        Response response = new Response();

        response.setRequestId(requestId);

        response.setResponse(invoke);

        log.info("打印返回信息[{}]", response);

        channelHandlerContext.pipeline().writeAndFlush(response);

    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

        LOGGER.error("Exception caught on {}, ", ctx.channel(), cause);

        ctx.channel().close();
    }
}
