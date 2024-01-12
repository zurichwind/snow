package cn.zurish.snow.rpc.rpc.common.convention.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 2024/1/11 21:27
 */
@Getter
@AllArgsConstructor
public class SnowException extends RuntimeException{

    private Integer code = 500;

    private final String message;

    public SnowException(String message) {
        this.message = message;
    }

    public SnowException(Integer code) {
        this.code = code;
        this.message = "message error!";
    }
}
