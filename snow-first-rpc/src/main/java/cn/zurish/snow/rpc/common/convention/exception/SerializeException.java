package cn.zurish.snow.rpc.common.convention.exception;

import cn.zurish.snow.rpc.common.convention.errorcode.ErrorCode;
import cn.zurish.snow.rpc.common.convention.errorcode.RpcErrorCode;

/**
 * 2024/1/11 21:28
 */
public class SerializeException extends AbstractException{
    public SerializeException(ErrorCode errorCode) {
        this(null, null, errorCode);
    }

    public SerializeException(String message) {
        this(message, null, RpcErrorCode.RPC_ERROR);
    }

    public SerializeException(String message, ErrorCode errorCode) {
        this(message, null, errorCode);
    }

    public SerializeException(String message, Throwable throwable, ErrorCode errorCode) {
        super(message, throwable, errorCode);
    }

    @Override
    public String toString() {
        return "SerializeException{" +
                "code='" + errorCode + "'," +
                "message='" + errorMessage + "'" +
                '}';
    }
}
