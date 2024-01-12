package cn.zurish.snow.rpc.rpc.serializer;

/**
 * 序列化接口
 * 2024/1/12 10:18
 */
public interface Serializer {

    <T> byte[] serialize(T obj);

    <T> T deserialize(byte[] data, Class<?> clazz);

    byte getCode();
}
