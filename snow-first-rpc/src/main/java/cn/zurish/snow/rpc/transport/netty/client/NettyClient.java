package cn.zurish.snow.rpc.transport.netty.client;

import cn.zurish.snow.rpc.common.convention.errorcode.RpcErrorCode;
import cn.zurish.snow.rpc.common.convention.exception.RpcException;
import cn.zurish.snow.rpc.common.entity.SnowRequest;
import cn.zurish.snow.rpc.common.entity.SnowResponse;
import cn.zurish.snow.rpc.core.loadbalance.LoadBalancer;
import cn.zurish.snow.rpc.core.loadbalance.RandomLoadBalancer;
import cn.zurish.snow.rpc.registry.NacosServiceDiscovery;
import cn.zurish.snow.rpc.registry.ServiceDiscovery;
import cn.zurish.snow.rpc.common.enums.SerializerType;
import cn.zurish.snow.rpc.factory.SingletonFactory;
import cn.zurish.snow.rpc.serializer.Serializer;
import cn.zurish.snow.rpc.transport.RpcClient;
import com.alibaba.fastjson2.JSON;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;

/**
 * NIO 方式消费侧客户端
 * 2024/1/13 20:21
 */
@Slf4j
public class NettyClient implements RpcClient {

    private static final EventLoopGroup group;

    private static final Bootstrap bootstrap;

    static {
        group = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class);
    }

    private final ServiceDiscovery serviceDiscovery;

    private final Serializer serializer;

    private final UnprocessedRequests unprocessedRequests;

    public NettyClient() {
        this(DEFAULT_SERIALIZER, new RandomLoadBalancer());
    }

    public NettyClient(LoadBalancer loadBalancer) {
        this(DEFAULT_SERIALIZER, loadBalancer);
    }

    public NettyClient(Byte serializer) {
        this(serializer, new RandomLoadBalancer());
    }

    public NettyClient(Byte serializer, LoadBalancer loadBalancer) {
        this.serviceDiscovery = new NacosServiceDiscovery(loadBalancer);
        this.serializer = SerializerType.getByCode(serializer);
        this.unprocessedRequests = SingletonFactory.getInstance(UnprocessedRequests.class);
    }
    @Override
    public Object sendRequest(SnowRequest request) {
        if (serializer == null) {
            log.error("未设置序列化器");
            throw new RpcException(RpcErrorCode.SERIALIZER_NOT_FOUND);
        }
        CompletableFuture<SnowResponse<?>> resultFuture = new CompletableFuture<>();
        try {
            InetSocketAddress inetSocketAddress = serviceDiscovery.lookupService(request.getInterfaceName());
            Channel channel = ChannelProvider.get(inetSocketAddress, serializer);
            if (channel != null && !channel.isActive()) {
                group.shutdownGracefully();
                return null;
            }
            unprocessedRequests.put(request.getRequestId(), resultFuture);
            if (channel != null) {
                channel.writeAndFlush(request).addListener((ChannelFutureListener) future -> {
                    if (future.isSuccess()) {
                        log.info(String.format("客户端发送消息: %s", JSON.toJSONString(request)));
                    } else {
                        future.channel().close();
                        resultFuture.completeExceptionally(future.cause());
                        log.error("发送消息时有错误发生: ", future.cause());
                    }
                });
            }
        } catch (InterruptedException e) {
            unprocessedRequests.remove(request.getRequestId());
            log.error(e.getMessage(), e);
            Thread.currentThread().interrupt();
        }
        return resultFuture;
    }
}
