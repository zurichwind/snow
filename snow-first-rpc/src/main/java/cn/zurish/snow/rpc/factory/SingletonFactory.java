package cn.zurish.snow.rpc.factory;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * 单例工厂
 * 2024/1/14 10:54:56
 */
@Slf4j
public class SingletonFactory {

    private static final Map<Class<?>, Object> objectMap = new HashMap<>();

    private SingletonFactory() {};

    public static <T> T getInstance(Class<T> clazz) {
        Object instance = objectMap.get(clazz);
        synchronized (clazz) {
            if (instance == null ) {
                try {
                    instance = clazz.getDeclaredConstructor().newInstance();
                    objectMap.put(clazz, instance);
                } catch (InvocationTargetException | InstantiationException | IllegalAccessException |
                         NoSuchMethodException e) {
                    log.info("创建单例模式失败:{}",clazz);
                    throw new RuntimeException(e.getMessage(), e);
                }
            }
        }
        return clazz.cast(instance);
    }
}
