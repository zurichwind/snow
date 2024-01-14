package cn.zurish.snow.rpc.test.service;

import cn.zurish.snow.rpc.annotation.SnowService;
import cn.zurish.snow.rpc.test.api.ByeService;

/**
 * 2024/1/14 15:30:28
 */
@SnowService
public class ByeServiceImpl implements ByeService {
    @Override
    public String bye(String name) {
        return "bye ====> : " + name;
    }
}
