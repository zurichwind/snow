package cn.zurish.snow.rpc.rpc.common.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 消费者发送的请求对象
 * 2024/1/11 20:48
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SnowRequest extends RpcEntity implements Serializable {

    private static final long serialVersionUID = 2024;

    /**
     * 请求号
     */
    private String requestId;
    /**
     * 待调用接口名称
     */
    private String interfaceName;
    /**
     * 待调用方法名称
     */
    private String methodName;
    /**
     * 调用方法的参数
     */
    private Object[] parameters;
    /**
     * 调用方法的参数类型
     */
    private Class<?>[] paramTypes;

    /**
     * 是否是心跳包
     */
    private Boolean heartBeat;
}
