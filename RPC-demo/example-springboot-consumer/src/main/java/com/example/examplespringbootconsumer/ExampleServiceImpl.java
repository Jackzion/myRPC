package com.example.examplespringbootconsumer;

import com.example.rpcstarter.annotation.RpcReference;
import com.ziio.example.common.model.User;
import com.ziio.example.common.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class ExampleServiceImpl {

    @RpcReference
    private UserService userService;

    public void test() {
        User user = new User();
        user.setName("ZIIO");
        User resultUser = userService.getUser(user);
        System.out.println(resultUser.getName());
    }

}