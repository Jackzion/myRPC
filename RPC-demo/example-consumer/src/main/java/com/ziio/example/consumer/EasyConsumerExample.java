package com.ziio.example.consumer;

import com.ziio.example.common.model.User;
import com.ziio.example.common.service.UserService;

public class EasyConsumerExample {
    public static void main(String[] args) {
//        UserService userService = null;
//        User user = new User();
//        user.setName("ziio");
//        // 调用
//        User newUser = userService.getUser(user);
//        if(newUser!=null){
//            System.out.println(newUser.getName());
//        }else{
//            System.out.println("User == null");
//        }
        // 静态代理发送请求 (每个service一个代理，麻烦)
        UserServiceProxy userServiceProxy = new UserServiceProxy();
        User user = new User();
        user.setName("ziio");
        System.out.println(userServiceProxy.getUser(user).getName());
    }
}
