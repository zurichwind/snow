package cn.zurish.snow.rpc.common.entity;

/**
 * 2024/1/12 14:37
 */

public class RpcMessage {

    private byte messageType;

    private byte serializer;

    private byte compress;

    private int requestId;

    private Object data;
}
