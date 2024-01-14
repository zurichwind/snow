package cn.zurish.snow.rpc.common.convention.errorcode;

/**
 * 2024/1/12 11:24
 */
public enum RpcErrorCode implements ErrorCode {
    RPC_ERROR("A000001", "Rpc错误"),
    UNSUPPORT_PROTOCOL("R000002", "不支持的协议包"),
    UNMATCHED_VERSION("R000003", "不匹配的版本号"),
    UNKNOWN_MESSAGE_TYPE("R000004", "不识别的协议数据包"),
    UNKNOWN_SERIALIZER("R000005", "不识别的序列化协议" ),
    SERVER_NOT_FOUND("R000007","找不到对应的服务"),
    REGISTER_SERVICE_FAILED("R000008","注册服务失败" ),
    SERVICE_NOT_FOUND("R000009", "找不到对应的服务" ),
    SERVICE_INVOCATION_FAILURE("R000010","服务调用失败" ),
    RESPONSE_NOT_MATCH("R000011","响应与请求号不匹配" ),
    SERVICE_SCAN_PACKAGE_NOT_FOUND("R000012", "启动类ServiceScan注解缺失" ),
    UNKNOWN_ERROR("R000013", "出现未知错误" ),
    SERIALIZER_NOT_FOUND("R000014", "找不到对应的序列化器" ),
    UNKNOWN_PACKAGE_TYPE("R000015", "不识别的协议数据包" );

    private final String code;

    private final String message;

    RpcErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String code() {
        return code;
    }

    @Override
    public String message() {
        return message;
    }
}
