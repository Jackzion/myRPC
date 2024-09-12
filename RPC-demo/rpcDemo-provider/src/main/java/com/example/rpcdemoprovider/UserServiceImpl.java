package com.example.rpcdemoprovider;

import com.example.rpcstarter.annotation.RpcService;
import com.ziio.example.common.model.User;
import com.ziio.example.common.service.UserService;
import org.springframework.stereotype.Service;

@Service
@RpcService
public class UserServiceImpl implements UserService {

    public User getUser(User user) {
        System.out.println("用户名：" + user.getName());
        return user;
    }
}