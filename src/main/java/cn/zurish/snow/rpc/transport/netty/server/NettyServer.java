package cn.zurish.snow.rpc.transport.netty.server;

import cn.zurish.snow.rpc.codec.SnowDecoder;
import cn.zurish.snow.rpc.codec.SnowEncoder;
import cn.zurish.snow.rpc.hook.ShutDownHook;
import cn.zurish.snow.rpc.provider.ServiceProviderImpl;
import cn.zurish.snow.rpc.registry.NacosServiceRegistry;
import cn.zurish.snow.rpc.common.enums.SerializerType;
import cn.zurish.snow.rpc.serializer.Serializer;
import cn.zurish.snow.rpc.transport.AbstractRpcServer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * NIO方式 服务提供侧
 * 2024/1/13 20:26
 */
@Slf4j
public class NettyServer extends AbstractRpcServer {

    private final Serializer serializer;

    public NettyServer(String host, int port) {
        this(host, port, SerializerType.KRYO_SERIALIZER.getCode());

    }

    public NettyServer(String host, int port, Byte serializer) {
        this.host = host;
        this.port = port;
        serviceRegistry = new NacosServiceRegistry();
        serviceProvider = new ServiceProviderImpl();
        this.serializer = SerializerType.getByCode(serializer);
        scanServices();

    }
    @Override
    public void start() {
        ShutDownHook.getShutDownHook().addClearAllHook();


        try (EventLoopGroup bossGroup = new NioEventLoopGroup();
             EventLoopGroup workerGroup = new NioEventLoopGroup()){
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .option(ChannelOption.SO_BACKLOG, 256)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new IdleStateHandler(30, 0, 0 , TimeUnit.SECONDS))
                                    .addLast(new SnowEncoder(serializer))
                                    .addLast(new SnowDecoder())
                                    .addLast(new NettyServerHandler());
                        }
                    });
            ChannelFuture future = serverBootstrap.bind(host, port).sync();
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("启动服务器时有错误发生: ", e);
        }
    }
}
