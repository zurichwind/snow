package cn.zurish.snow.rpc.rpc.common.convention.result;

import lombok.Data;

/**
 * 2024/1/12 12:52
 */
@Data
public class Result<T> {

    private static final Integer SUCCESS = 500;

    private  Integer code;

    private String message;

    private T data;

    //private String requestId;

    public static <T> Result<T> success(StatusCodeEnum code) {
        return restResult(code.getCode(), code.getMessage());
    }

    public static <T> Result<T> success(T data) {
        return restResult(StatusCodeEnum.SUCCESS.getCode(), StatusCodeEnum.SUCCESS.getMessage(),data);
    }

    public static <T> Result<T> success(T data, String message) {
        return restResult(StatusCodeEnum.SUCCESS.getCode(), message, data);
    }

    public static <T> Result<T> fail() {
        return restResult(StatusCodeEnum.FAIL.getCode(), StatusCodeEnum.FAIL.getMessage(), null);
    }

    public static <T> Result<T> fail(StatusCodeEnum code) {
        return restResult(code.getCode(), code.getMessage(), null);
    }

    public static <T> Result<T> fail(String message) {
        return restResult(StatusCodeEnum.FAIL.getCode(), message, null);
    }

    public static <T> Result<T> fail(T data) {
        return restResult(StatusCodeEnum.FAIL.getCode(),StatusCodeEnum.FAIL.getMessage(), data);
    }

    public static <T> Result<T> fail(Integer code, String message) {
        return restResult(code,message);
    }
    public static <T> Result<T> fail(Integer code, String message, T data) {
        return restResult(code,message, data);
    }

    public static <T> Result<T> fail(T data, String message) {
        return restResult(StatusCodeEnum.FAIL.getCode(),message, data);
    }

    private static <T> Result<T> restResult(Integer code, String message) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);

        return result;
    }

    private static <T> Result<T> restResult(Integer code, String message, T data) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        result.setData(data);

        return result;
    }


}
