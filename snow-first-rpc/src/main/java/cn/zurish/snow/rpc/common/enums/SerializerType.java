package cn.zurish.snow.rpc.common.enums;


import cn.zurish.snow.rpc.serializer.*;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 2024/1/12 14:23
 */
@AllArgsConstructor
@Getter
public enum SerializerType {

    KRYO_SERIALIZER((byte) 0x01, "kryo"),
    JSON_SERIALIZER((byte) 0x02, "json"),
    HESSIAN_SERIALIZER((byte) 0x03, "hessian"),
    PROTOBUF_SERIALIZER((byte) 0x04, "protobuf");


    private final Byte code;

    private final String name;

    public static Serializer getByCode(byte code) {
        return switch (code) {
            case 0x01 -> new KryoSerializer();
            case 0x02 -> new JsonSerializer();
            case 0x03 -> new HessianSerializer();
            case 0x04 -> new ProtobufSerializer();
            default -> null;
        };
    }

}
