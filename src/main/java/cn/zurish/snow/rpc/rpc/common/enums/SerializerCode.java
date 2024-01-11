package cn.zurish.snow.rpc.rpc.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 序列化协议枚举
 * 2024/1/11 20:55
 */
@AllArgsConstructor
@Getter
public enum SerializerCode {

    KRYO(0),
    JSON(1),
    HESSIAN(2),
    PROTOBUF(3);

    private final int code;

}