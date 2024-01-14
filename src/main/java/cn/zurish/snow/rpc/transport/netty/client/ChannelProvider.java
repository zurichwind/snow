package cn.zurish.snow.rpc.transport.netty.client;

import cn.zurish.snow.rpc.codec.SnowDecoder;
import cn.zurish.snow.rpc.codec.SnowEncoder;
import cn.zurish.snow.rpc.serializer.Serializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * 2024/1/13 20:35
 */
@Slf4j
public class ChannelProvider {

    private static EventLoopGroup eventLoopGroup;

    private static Bootstrap bootstrap = initializeBootstrap();

    private static Map<String, Channel> channelMaps = new ConcurrentHashMap<>();

    public static Channel get (InetSocketAddress inetSocketAddress, Serializer serializer) throws InterruptedException {
        String key = inetSocketAddress.toString() + serializer.getCode();
        if (channelMaps.containsKey(key)) {
            Channel channel = channelMaps.get(key);
            if (channelMaps != null && channel.isActive()) {
                return channel;
            } else {
                if (channelMaps != null) {
                    channelMaps.remove(key);
                }
            }
        }

        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                //自定义序列化解码器
                //SnowResponse -> ByteBuf
                ch.pipeline().addLast(new SnowEncoder(serializer))
                        .addLast(new IdleStateHandler(0, 5, 0, TimeUnit.SECONDS))
                        .addLast(new SnowDecoder())
                        .addLast(new NettyClientHandler());
            }
        });
        Channel channel = null;
        try {
            channel = connect(bootstrap, inetSocketAddress);
        } catch (ExecutionException e) {
            log.error("连接客户端时有错误发生:",e);
            return null;
        }
        channelMaps.put(key, channel);
        return channel;
    }

    private static Channel connect(Bootstrap bootstrap, InetSocketAddress inetSocketAddress) throws ExecutionException, InterruptedException {
        CompletableFuture<Channel> completableFuture = new CompletableFuture<>();
        bootstrap.connect(inetSocketAddress).addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {
                log.info("客户端连接成功!");
                completableFuture.complete(future.channel());
            } else {
                throw new IllegalStateException();
            }
        });
        return completableFuture.get();
    }

    private static Bootstrap initializeBootstrap() {
        eventLoopGroup = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                //连接的超时时间，超过这个时间还是建立不上的话则代表连接失败
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                //是否开启了 TCP 底层心跳机制
                .option(ChannelOption.SO_KEEPALIVE, true)
                // TCP默认开启了 Nagle 算法，该算法的作用是尽可能的发生大数据块，减少网络传输。TCP_NODELAY
                .option(ChannelOption.TCP_NODELAY, true);
        return bootstrap;
    }
}
