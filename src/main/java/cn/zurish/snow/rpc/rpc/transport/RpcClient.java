package cn.zurish.snow.rpc.rpc.transport;

import cn.zurish.snow.rpc.rpc.common.entity.SnowRequest;
import cn.zurish.snow.rpc.rpc.common.enums.SerializerType;

/**
 * 客户端类通用接口
 * 2024/1/13 16:02
 */
public interface RpcClient {

    byte DEFAULT_SERIALIZER = SerializerType.KRYO_SERIALIZER.getCode();

    Object sendRequest(SnowRequest request);
}
