package cn.zurish.snow.rpc.transport.netty.server;

import cn.zurish.snow.rpc.common.entity.SnowRequest;
import cn.zurish.snow.rpc.common.entity.SnowResponse;
import cn.zurish.snow.rpc.handler.RequestHandler;
import cn.zurish.snow.rpc.factory.SingletonFactory;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * Netty中处理SnowRequest的Handler
 * 2024/1/13 20:27
 */
@Slf4j
public class NettyServerHandler extends SimpleChannelInboundHandler<SnowRequest> {

    private final RequestHandler requestHandler;

    public NettyServerHandler() {
        this.requestHandler = SingletonFactory.getInstance(RequestHandler.class);
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, SnowRequest msg) throws Exception {
       try {
           if (msg.getHeartBeat()) {
               log.info("===========> 接收到客户端心跳包...");
               return;
           }
           log.info("服务器收到请求: {}", msg);
           Object result = requestHandler.handle(msg);
           if (ctx.channel().isActive() && ctx.channel().isWritable()) {
               ctx.writeAndFlush(SnowResponse.success(result, msg.getRequestId()));
           } else {
               log.info("通道不可写,,,");
           }
       } finally {
           ReferenceCountUtil.release(msg);
       }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("处理过程调用时有错误发生: ", cause);
        ctx.close();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            if (state == IdleState.READER_IDLE) {
                log.info("长时间未收到心跳包, 断开连接...");
                ctx.close();
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
}
