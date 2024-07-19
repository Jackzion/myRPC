package com.ziio.example.consumer;

import com.ziio.example.bootstrap.ConsumerBootstrap;
import com.ziio.example.common.model.User;
import com.ziio.example.common.service.UserService;
import com.ziio.example.config.RpcConfig;
import com.ziio.example.proxy.ServiceProxyFactory;
import com.ziio.example.utils.ConfigUtils;

public class EasyConsumerExample {
    public static void main(String[] args) {

        // 初始化配置
        ConsumerBootstrap.init();

//        // mock 代理
        UserService userService = ServiceProxyFactory.getProxy(UserService.class);
        User user = new User();
        user.setName("ziio");

        // 调用
        User newUser = userService.getUser(user);
        if (newUser != null) {
            System.out.println(newUser.getName());
        } else {
            System.out.println("user == null");
        }

    }
}
