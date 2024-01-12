package cn.zurish.snow.rpc.rpc.serializer;

/**
 * 2024/1/12 14:29
 */
public class HessianSerializer implements Serializer{
    @Override
    public <T> byte[] serialize(T obj) {
        return new byte[0];
    }

    @Override
    public <T> T deserialize(byte[] data, Class<?> clazz) {
        return null;
    }

    @Override
    public byte getCode() {
        return 0x03;
    }
}
