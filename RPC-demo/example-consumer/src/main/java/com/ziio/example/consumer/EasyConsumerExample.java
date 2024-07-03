package com.ziio.example.consumer;

import com.ziio.example.common.model.User;
import com.ziio.example.common.service.UserService;

public class EasyConsumerExample {
    public static void main(String[] args) {
        // 静态代理发送请求 (每个service一个代理，麻烦)
//        ServiceProxy serviceProxy = new ServiceProxy();
        // 动态代理
        UserService userService = ServiceProxyFactory.getProxy(UserService.class);
        User user = new User();
        user.setName("ziio");
        System.out.println(userService.getUser(user).getName());
    }
}
