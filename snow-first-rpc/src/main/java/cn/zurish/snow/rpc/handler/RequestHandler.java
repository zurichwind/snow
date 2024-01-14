package cn.zurish.snow.rpc.handler;

import cn.zurish.snow.rpc.common.entity.SnowRequest;
import cn.zurish.snow.rpc.common.entity.SnowResponse;
import cn.zurish.snow.rpc.common.enums.ResponseCode;
import cn.zurish.snow.rpc.provider.ServiceProvider;
import cn.zurish.snow.rpc.provider.ServiceProviderImpl;
import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 进行过程调用的处理器
 * 2024/1/13 13:06
 */
@Slf4j
public class RequestHandler {

    private static final ServiceProvider serviceProvider;

    static {
        serviceProvider = new ServiceProviderImpl();
    }

    public Object handle(SnowRequest request) {
        Object service = serviceProvider.getServiceProvider(request.getInterfaceName());
        return invokeTargetMethod(request, service);
    }

    private Object invokeTargetMethod(SnowRequest request, Object service) {
        Object result;
        try {
            Method method = service.getClass().getMethod(request.getMethodName(), request.getParamTypes());
            result = method.invoke(service, request.getParameters());
            log.info("服务: {} 成功调用方法: {}",service, request.getParameters());
            log.info("request: "+JSON.toJSONString(request));
        } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
            log.info("request: "+JSON.toJSONString(request));
            return SnowResponse.fail(ResponseCode.METHOD_NOT_FOUND, request.getRequestId());
        }
        return result;
    }

}
