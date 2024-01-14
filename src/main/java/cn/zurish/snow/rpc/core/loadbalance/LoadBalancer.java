package cn.zurish.snow.rpc.core.loadbalance;

import com.alibaba.nacos.api.naming.pojo.Instance;

import java.util.List;

/**
 * 负载均衡接口
 * 2024/1/11 21:39
 */
public interface LoadBalancer {
    Instance select(List<Instance> instances);
}
