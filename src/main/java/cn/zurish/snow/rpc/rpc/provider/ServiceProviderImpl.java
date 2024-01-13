package cn.zurish.snow.rpc.rpc.provider;

import cn.zurish.snow.rpc.rpc.common.convention.errorcode.RpcErrorCode;
import cn.zurish.snow.rpc.rpc.common.convention.exception.RpcException;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 默认的服务注册表，保存服务端本地服务
 * 2024/1/12 20:18
 */
@Slf4j
public class ServiceProviderImpl implements ServiceProvider{

    private static final Map<String, Object> serviceMap = new ConcurrentHashMap<String, Object>();

    private static final Set<String> registeredService = ConcurrentHashMap.newKeySet();


    @Override
    public <T> void addServiceProvider(T service, String serviceName) {
        if (registeredService.contains(serviceName)) return;
        registeredService.add(serviceName);
        serviceMap.put(serviceName, service);
        log.info("向接口: {} 注册服务: {}", service.getClass().getInterfaces(), serviceName);
    }

    @Override
    public Object getServiceProvider(String serviceName) {
        Object service = serviceMap.get(serviceName);
        if (service == null) {
            log.info("找不到对应的服务: {}", serviceName);
            throw new RpcException(RpcErrorCode.SERVICE_NOT_FOUND);
        }
        return service;
    }
}
