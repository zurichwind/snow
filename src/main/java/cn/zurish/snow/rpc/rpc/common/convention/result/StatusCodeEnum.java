package cn.zurish.snow.rpc.rpc.common.convention.result;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 2024/1/12 13:00
 */
@Getter
@AllArgsConstructor
public enum StatusCodeEnum {
    SUCCESS(200, "成功"),

    FAIL(500,"失败");

    private final Integer code;

    private final String message;



}
