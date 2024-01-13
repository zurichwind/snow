package cn.zurish.snow.rpc.rpc.transport.netty.server;

import cn.zurish.snow.rpc.rpc.common.entity.SnowRequest;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * Netty中处理SnowRequest的Handler
 * 2024/1/13 20:27
 */
@Slf4j
public class NettyServerHandler extends SimpleChannelInboundHandler<SnowRequest> {


    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, SnowRequest request) throws Exception {

    }
}
