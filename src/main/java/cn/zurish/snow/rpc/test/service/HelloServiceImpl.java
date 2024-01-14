package cn.zurish.snow.rpc.test.service;

import cn.zurish.snow.rpc.annotation.SnowService;
import cn.zurish.snow.rpc.test.api.HelloObject;
import cn.zurish.snow.rpc.test.api.HelloService;
import lombok.extern.slf4j.Slf4j;

/**
 * 2024/1/14 15:34:49
 */
@SnowService
@Slf4j
public class HelloServiceImpl implements HelloService {
    @Override
    public String hello(HelloObject object) {
        log.info("接收到消息: {}", object.getMessage());
        return "hello love ling";
    }
}
