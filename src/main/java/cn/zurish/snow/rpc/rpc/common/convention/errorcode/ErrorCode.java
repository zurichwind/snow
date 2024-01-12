package cn.zurish.snow.rpc.rpc.common.convention.errorcode;

/**
 * 错误码基类
 * 2024/1/12 11:22
 */
public interface ErrorCode {


    /**
     * 错误码
     */
    String code();

    /**
     * 错误信息
     */
    String message();

}
