package cn.zurish.snow.rpc.transport;

import cn.zurish.snow.rpc.common.entity.SnowRequest;
import cn.zurish.snow.rpc.common.entity.SnowResponse;
import cn.zurish.snow.rpc.transport.netty.client.NettyClient;
import cn.zurish.snow.rpc.transport.socket.SocketClient;
import cn.zurish.snow.rpc.utils.RpcMessageChecker;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**RPC 客户端动态代理
 * 2024/1/13 20:05
 */
@Slf4j
public class RpcClientProxy implements InvocationHandler {

    private final RpcClient client;

    public RpcClientProxy(RpcClient client) {
        this.client = client;
    }

    @SuppressWarnings("unchecked")
    public <T> T getProxy(Class<T> clazz) {
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class<?>[]{clazz}, this);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object invoke(Object proxy, Method method, Object[] args){
        log.info("调用方法: {}#{}", method.getDeclaringClass().getName(), method.getName());
        SnowRequest request = new SnowRequest(UUID.randomUUID().toString(), method.getDeclaringClass().getName(),
                method.getName(), args, method.getParameterTypes(), false);
        SnowResponse<?> snowResponse = null;
        if (client instanceof NettyClient) {
            try {
                CompletableFuture<SnowResponse<?>> completableFuture = (CompletableFuture<SnowResponse<?>>) client.sendRequest(request);
                snowResponse = completableFuture.get();
            } catch (Exception e) {
                log.error("方法调用请求发送失败", e);
                return null;
            }
        }
        if (client instanceof SocketClient) {
            snowResponse = (SnowResponse<?>) client.sendRequest(request);
        }
        RpcMessageChecker.check(request, snowResponse);
        return snowResponse.getData();
    }
}
