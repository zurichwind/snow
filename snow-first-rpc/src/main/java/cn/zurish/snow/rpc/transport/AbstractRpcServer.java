package cn.zurish.snow.rpc.transport;

import cn.zurish.snow.rpc.annotation.SnowService;
import cn.zurish.snow.rpc.annotation.SnowServiceScan;
import cn.zurish.snow.rpc.common.convention.errorcode.RpcErrorCode;
import cn.zurish.snow.rpc.common.convention.exception.RpcException;
import cn.zurish.snow.rpc.provider.ServiceProvider;
import cn.zurish.snow.rpc.registry.ServiceRegistry;
import cn.zurish.snow.rpc.utils.ReflectUtils;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.net.InetSocketAddress;
import java.util.Set;

/**
 * 2024/1/13 17:20
 */
@Slf4j
public abstract class AbstractRpcServer implements RpcServer{

    protected String host;

    protected int port;

    protected ServiceRegistry serviceRegistry;

    protected ServiceProvider serviceProvider;

    public void scanServices() {
        String mainClassName = ReflectUtils.getStackTrace();
        Class<?> startClass;
        try {
            startClass = Class.forName(mainClassName);
            if (!startClass.isAnnotationPresent(SnowServiceScan.class)) {
                log.error("启动类缺少 @SnowServiceScan 注解");
                throw new RpcException(RpcErrorCode.SERVICE_SCAN_PACKAGE_NOT_FOUND);
            }
        } catch (ClassNotFoundException e) {
            log.error("未知错误, 找不到对应的类");
            throw new RpcException(RpcErrorCode.UNKNOWN_ERROR);
        }

        String basePackages = startClass.getAnnotation(SnowServiceScan.class).value();
        if ("".equals(basePackages)) {
            basePackages = mainClassName.substring(0, mainClassName.lastIndexOf("."));
        }
        Set<Class<?>> classSet = ReflectUtils.getClasses(basePackages);
        for (Class<?> clazz : classSet) {
            if (clazz.isAnnotationPresent(SnowService.class)) {
                String serviceName = clazz.getAnnotation(SnowService.class).serviceName();
                Object obj;
                try {
                    obj = clazz.getDeclaredConstructor().newInstance();
                } catch (InvocationTargetException | IllegalAccessException | InstantiationException |
                         NoSuchMethodException e) {
                    log.error("创建 " + clazz + " 时有错误发生");
                    continue;
                }
                if ("".equals(serviceName)) {
                    Class<?>[] interfaces = clazz.getInterfaces();
                    for (Class<?> oneInterface : interfaces) {
                        publishService(obj, oneInterface.getCanonicalName());
                    }
                } else {
                    publishService(obj, serviceName);
                }
            }
        }
    }

    @Override
    public <T> void publishService(T service, String serviceName) {
        serviceProvider.addServiceProvider(service, serviceName);
        serviceRegistry.register(serviceName, new InetSocketAddress(host, port));
    }
}
