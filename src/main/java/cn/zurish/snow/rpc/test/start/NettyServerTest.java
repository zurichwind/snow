package cn.zurish.snow.rpc.test.start;

import cn.zurish.snow.rpc.annotation.SnowServiceScan;
import cn.zurish.snow.rpc.common.enums.SerializerType;
import cn.zurish.snow.rpc.transport.RpcServer;
import cn.zurish.snow.rpc.transport.netty.server.NettyServer;

/**
 * 2024/1/14 15:37:35
 */
@SnowServiceScan("cn.zurish.snow.rpc.test.service")
public class NettyServerTest {

    public static void main(String[] args) {
        RpcServer server = new NettyServer("127.0.0.1", 2006, SerializerType.KRYO_SERIALIZER.getCode());
        server.start();
    }
}
