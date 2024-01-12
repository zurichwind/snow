package cn.zurish.snow.rpc.rpc.common.enums;

import cn.zurish.snow.rpc.rpc.common.convention.errorcode.RpcErrorCode;
import cn.zurish.snow.rpc.rpc.common.convention.exception.RpcException;
import cn.zurish.snow.rpc.rpc.common.entity.RpcEntity;
import cn.zurish.snow.rpc.rpc.common.entity.SnowRequest;
import cn.zurish.snow.rpc.rpc.common.entity.SnowResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 2024/1/12 10:22
 */
@AllArgsConstructor
@Getter
public enum RequestType {

    REQUEST_TYPE((byte) 0x01),
    RESPONSE_TYPE((byte) 0x02),
    HEARTBEAT_REQUEST_TYPE((byte) 0x03),
    HEARTBEAT_RESPONSE_TYPE((byte) 0x04);

    private final int code;

    public static Object getByCode(byte code) {
        return switch (code) {
            case 0x01 -> new SnowRequest();
            case 0x02 -> new SnowResponse<Object>();
            default -> throw new RpcException(RpcErrorCode.UNKNOWN_MESSAGE_TYPE);
        };
    }

}
