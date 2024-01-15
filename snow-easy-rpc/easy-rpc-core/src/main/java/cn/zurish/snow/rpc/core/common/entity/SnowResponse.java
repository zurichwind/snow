package cn.zurish.snow.rpc.core.common.entity;

import cn.zurish.snow.rpc.core.common.enums.ResponseCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 2024/1/15 15:23:19
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SnowResponse<T> implements Serializable {

    private static final long serialVersionUID = 2000;

    /**
     * 响应对应的请求号
     */
    private String requestId;
    /**
     * 响应状态码
     */
    private Integer statusCode;
    /**
     * 响应状态补充信息
     */
    private String message;
    /**
     * 响应数据
     */
    private T data;

    public static <T> SnowResponse<T> success(T data, String requestId) {
        SnowResponse<T> response = new SnowResponse<>();
        response.setRequestId(requestId);
        response.setStatusCode(ResponseCode.SUCCESS.getCode());
        response.setData(data);
        return response;
    }

    public static <T> SnowResponse<T> fail(ResponseCode code, String requestId) {
        SnowResponse<T> response = new SnowResponse<>();
        response.setRequestId(requestId);
        response.setStatusCode(code.getCode());
        response.setMessage(code.getMessage());
        return response;
    }
}
