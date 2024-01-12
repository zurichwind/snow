package cn.zurish.snow.rpc.rpc.registry;

import cn.zurish.snow.rpc.rpc.common.convention.errorcode.RpcErrorCode;
import cn.zurish.snow.rpc.rpc.common.convention.exception.RpcException;
import cn.zurish.snow.rpc.rpc.utils.NacosUtils;
import com.alibaba.nacos.api.exception.NacosException;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

/**
 * 2024/1/12 15:59
 */
@Slf4j
public class NacosServiceRegistry implements ServiceRegistry{
    @Override
    public void register(String serviceName, InetSocketAddress inetSocketAddress) {
        try {
            NacosUtils.registerService(serviceName, inetSocketAddress);
        } catch (NacosException e) {
            log.error("注册服务时有错误发生: ", e);
            throw new RpcException(RpcErrorCode.REGISTER_SERVICE_FAILED);
        }
    }
}
