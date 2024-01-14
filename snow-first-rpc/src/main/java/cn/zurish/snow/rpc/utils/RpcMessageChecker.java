package cn.zurish.snow.rpc.utils;

import cn.zurish.snow.rpc.common.convention.errorcode.RpcErrorCode;
import cn.zurish.snow.rpc.common.entity.SnowRequest;
import cn.zurish.snow.rpc.common.entity.SnowResponse;
import cn.zurish.snow.rpc.common.enums.ResponseCode;
import cn.zurish.snow.rpc.common.convention.exception.RpcException;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 检查响应与请求
 * 2024/1/13 13:57
 */
@Slf4j
@NoArgsConstructor
public class RpcMessageChecker {

    public static final String INSTANCE_NAME = "interfaceName";

    public static void check(SnowRequest request, SnowResponse response) {
        if (response == null) {
            log.error("调用服务失败, serviceName: {}", request.getInterfaceName());
            throw new RpcException( INSTANCE_NAME + ":"+ request.getInterfaceName(), RpcErrorCode.SERVICE_INVOCATION_FAILURE);
        }

        if (!request.getRequestId().equals(response.getRequestId())) {
            log.info("响应与请求号不匹配");
            throw new RpcException(INSTANCE_NAME + ":" +request.getInterfaceName(), RpcErrorCode.RESPONSE_NOT_MATCH);
        }

        if (response.getStatusCode() == null || response.getStatusCode().equals(ResponseCode.FAIL.getCode())) {
            log.error("调用服务失败, serviceName:{}, SnowResponse:{}", request.getInterfaceName(),response);
            throw new RpcException(INSTANCE_NAME + ":" + request.getInterfaceName(), RpcErrorCode.SERVICE_INVOCATION_FAILURE);
        }
    }
}
