package com.example.examplespringbootconsumer;

import com.example.rpcstarter.annotation.RpcService;
import com.ziio.example.common.model.User;
import com.ziio.example.common.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class ExampleServiceImpl {
    // 属性生成代理注入
    @RpcService
    private UserService userService;

    // 测试
    public void test() {
        User user = new User();
        user.setName("yupi");
        User resultUser = userService.getUser(user);
        System.out.println(resultUser.getName());
    }
}
