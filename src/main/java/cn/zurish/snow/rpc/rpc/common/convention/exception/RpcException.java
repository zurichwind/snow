package cn.zurish.snow.rpc.rpc.common.convention.exception;

import cn.zurish.snow.rpc.rpc.common.convention.errorcode.ErrorCode;
import cn.zurish.snow.rpc.rpc.common.convention.errorcode.RpcErrorCode;

/**
 * 2024/1/11 21:27
 */
public class RpcException extends AbstractException{

    public RpcException(ErrorCode errorCode) {
        this(null, null, errorCode);
    }

    public RpcException(String message) {
        this(message, null, RpcErrorCode.RPC_ERROR);
    }

    public RpcException(String message, ErrorCode errorCode) {
        this(message, null, errorCode);
    }

    public RpcException(String message, Throwable throwable, ErrorCode errorCode) {
        super(message, throwable, errorCode);
    }

    @Override
    public String toString() {
        return "RpcException{" +
                "code='" + errorCode + "'," +
                "message='" + errorMessage + "'" +
                '}';
    }
}
