package com.ziio.example.consumer;

import com.ziio.example.common.model.User;
import com.ziio.example.common.service.UserService;
import com.ziio.example.config.RpcConfig;
import com.ziio.example.proxy.ServiceProxyFactory;
import com.ziio.example.utils.ConfigUtils;

public class EasyConsumerExample {
    public static void main(String[] args) {
        // 静态代理发送请求 (每个service一个代理，麻烦)
//        ServiceProxy serviceProxy = new ServiceProxy();

        // 动态代理
//        UserService userService = ServiceProxyFactory.getProxy(UserService.class);
//        User user = new User();
//        user.setName("ziio");
//        System.out.println(userService.getUser(user).getName());

//        // mock 代理
        UserService userService = ServiceProxyFactory.getProxy(UserService.class);
        User user = new User();
        user.setName("ziio");
        System.out.println(userService.getUser(user));

//        // 配置文件读取 test
//        RpcConfig rpc = ConfigUtils.loadConfig(RpcConfig.class, "rpc");
//        System.out.println(rpc);
    }
}
