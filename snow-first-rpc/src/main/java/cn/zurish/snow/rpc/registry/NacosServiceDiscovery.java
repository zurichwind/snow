package cn.zurish.snow.rpc.registry;

import cn.zurish.snow.rpc.core.loadbalance.RandomLoadBalancer;
import cn.zurish.snow.rpc.common.convention.errorcode.RpcErrorCode;
import cn.zurish.snow.rpc.common.convention.exception.RpcException;
import cn.zurish.snow.rpc.core.loadbalance.LoadBalancer;
import cn.zurish.snow.rpc.utils.NacosUtils;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.pojo.Instance;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * 2024/1/12 16:38
 */
@Slf4j
public class NacosServiceDiscovery implements ServiceDiscovery{

    private final LoadBalancer loadBalancer;

    public NacosServiceDiscovery(LoadBalancer loadBalancer) {
        if (loadBalancer == null) this.loadBalancer = new RandomLoadBalancer();
        else this.loadBalancer = loadBalancer;
    }
    @Override
    public InetSocketAddress lookupService(String serviceName) {
        try {
            List<Instance> instances = NacosUtils.getAllInstance(serviceName);
            if (instances.isEmpty()) {
                log.error("找不到对应的服务: " + serviceName);
                throw new RpcException(RpcErrorCode.SERVER_NOT_FOUND);
            }
            Instance instance = loadBalancer.select(instances);
            return new InetSocketAddress(instance.getIp(), instance.getPort());
        } catch (NacosException e) {
            log.error("获取服务时有错误发生: ", e);
            throw new RuntimeException(e);
        }
    }
}
