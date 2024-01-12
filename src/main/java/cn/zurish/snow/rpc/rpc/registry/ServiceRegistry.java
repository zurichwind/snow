package cn.zurish.snow.rpc.rpc.registry;

import java.net.InetSocketAddress;

/**
 * 服务注册接口
 * 2024/1/11 22:50
 */
public interface ServiceRegistry {

    void register(String serviceName, InetSocketAddress inetSocketAddress);
}
