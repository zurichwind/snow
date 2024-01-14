package cn.zurish.snow.rpc.provider;

/**
 * 2024/1/12 17:21
 */
public interface ServiceProvider {

    <T> void addServiceProvider(T service, String serviceName);

    Object getServiceProvider(String serviceName);
}
