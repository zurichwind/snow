package cn.zurish.snow.rpc.core.common.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 2024/1/15 15:22:40
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SnowRequest implements Serializable {

    private static final long serialVersionUID = 2001;

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
     * 调用方法的参数类型
     */
    private Class<?>[] paramTypes;

    /**
     * 调用方法的参数
     */
    private Object[] parameters;
}
