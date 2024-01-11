package cn.zurish.snow.rpc.rpc.common.entity;

import cn.zurish.snow.rpc.common.enums.ResponseCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 提供者的回应对象
 * 2024/1/11 20:50
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SnowResponse<T>  implements Serializable {

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
