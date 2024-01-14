package cn.zurish.snow.rpc.test.start;

import cn.zurish.snow.rpc.common.enums.SerializerType;
import cn.zurish.snow.rpc.test.api.ByeService;
import cn.zurish.snow.rpc.test.api.HelloObject;
import cn.zurish.snow.rpc.test.api.HelloService;
import cn.zurish.snow.rpc.transport.RpcClient;
import cn.zurish.snow.rpc.transport.RpcClientProxy;
import cn.zurish.snow.rpc.transport.netty.client.NettyClient;

/**
 * 2024/1/14 15:37:24
 */
public class NettyClientTest {

    public static void main(String[] args) {
        RpcClient client = new NettyClient(SerializerType.KRYO_SERIALIZER.getCode());
        RpcClientProxy rpcClientProxy = new RpcClientProxy(client);
        HelloService helloService = rpcClientProxy.getProxy(HelloService.class);
        HelloObject object = new HelloObject(23, "meet my ling");
        String res = helloService.hello(object);
        System.out.println(res);
        ByeService byeService = rpcClientProxy.getProxy(ByeService.class);
        System.out.println(byeService.bye("玲"));

    }
}
