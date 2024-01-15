package cn.zurish.snow.rpc.core.common.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 2024/1/15 15:20:58
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServiceMeta implements Serializable {

    private static final long serialVersionUID = 2024;
    /**
     * 服务名
     */
    private String serviceName;
    /**
     * 服务者的ip
     */
    private String serviceHost;
    /**
     * 服务者的端口
     */
    private int servicePort;
    /**
     * 上传日期
     */
    private Long updateTime;
    /**
     * 活跃数即连接数（每次上传后重置为0）
     */
    private int actives;
    /**
     * 服务分组
     */
    private String group;
    /**
     * 服务权重
     */
    private int weight;


}
