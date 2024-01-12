package cn.zurish.snow.rpc.rpc.registry;

import java.net.InetSocketAddress;

/**
 * 服务发现接口
 * 2024/1/12 15:57
 */
public interface ServiceDiscovery {
    InetSocketAddress lookupService(String serviceName);
}
