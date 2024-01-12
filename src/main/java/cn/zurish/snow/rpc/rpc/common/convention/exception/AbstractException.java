package cn.zurish.snow.rpc.rpc.common.convention.exception;

import cn.zurish.snow.rpc.rpc.common.convention.errorcode.ErrorCode;
import cn.zurish.snow.rpc.rpc.utils.CommonUtils;
import lombok.Getter;

import java.util.Optional;

/**
 * 2024/1/12 11:18
 */
@Getter
public abstract class AbstractException extends RuntimeException{

    public final String errorCode;

    public final String errorMessage;

    public AbstractException(String message, Throwable throwable, ErrorCode  errorCode) {
        super(message, throwable);
        this.errorCode = errorCode.code();
        this.errorMessage = Optional.ofNullable(CommonUtils.hasLength(message) ? message : null).orElse(errorCode.message());
    }
}
