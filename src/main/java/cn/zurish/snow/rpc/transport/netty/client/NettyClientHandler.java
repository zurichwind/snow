package cn.zurish.snow.rpc.transport.netty.client;

import cn.zurish.snow.rpc.common.entity.SnowRequest;
import cn.zurish.snow.rpc.common.entity.SnowResponse;
import cn.zurish.snow.rpc.common.enums.SerializerType;
import cn.zurish.snow.rpc.factory.SingletonFactory;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

/**
 * Netty 客户端侧处理器
 * 2024/1/13 20:35
 */
@Slf4j
public class NettyClientHandler extends SimpleChannelInboundHandler<SnowResponse<?>> {

    private final UnprocessedRequests unprocessedRequests;

   public NettyClientHandler() {
       this.unprocessedRequests = SingletonFactory.getInstance(UnprocessedRequests.class);
   }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, SnowResponse<?> msg) throws Exception {
        try {
            log.info(String.format("客户端侧收到消息: %s", msg));
            unprocessedRequests.complete(msg);
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
       log.error("过程调用时有错误发生:", cause);
       ctx.close();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            if (state == IdleState.WRITER_IDLE) {
                log.info("发送心跳包 [{}]", ctx.channel().remoteAddress());
                Channel channel = ChannelProvider.get((InetSocketAddress) ctx.channel().remoteAddress(), SerializerType.getByCode(SerializerType.KRYO_SERIALIZER.getCode()));
                SnowRequest request = new SnowRequest();
                request.setHeartBeat(true);
                channel.writeAndFlush(request).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
}
