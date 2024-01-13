package cn.zurish.snow.rpc.rpc.transport;

import cn.zurish.snow.rpc.rpc.common.enums.SerializerType;
import cn.zurish.snow.rpc.rpc.serializer.Serializer;

/**
 * 服务器类通用接口
 * 2024/1/13 16:03
 */
public interface RpcServer {

    byte DEFAULT_SERIALIZER = SerializerType.KRYO_SERIALIZER.getCode();

    void start();
    <T> void publishService(T service, String serviceName);



}
