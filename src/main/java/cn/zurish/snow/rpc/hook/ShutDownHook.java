package cn.zurish.snow.rpc.hook;

import cn.zurish.snow.rpc.factory.ThreadPoolFactory;
import cn.zurish.snow.rpc.utils.NacosUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * 服务注销钩子
 * 2024/1/12 20:30
 */
@Slf4j
public class ShutDownHook {

    private static ShutDownHook shutDownHook = new ShutDownHook();

    public static ShutDownHook getShutDownHook() {
        return shutDownHook;
    }

    public void addClearAllHook() {
        log.info("关闭后将自动注销所有服务");
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            NacosUtils.clearRegistry();
            ThreadPoolFactory.shutDownAll();
        }));
    }

}
