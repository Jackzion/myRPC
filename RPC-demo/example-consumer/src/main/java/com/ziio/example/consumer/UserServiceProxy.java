package com.ziio.example.consumer;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.ziio.example.common.model.User;
import com.ziio.example.common.service.UserService;
import com.ziio.example.consumer.serializer.JdkSerializer;
import com.ziio.example.consumer.serializer.Serializer;
import com.ziio.example.model.RpcRequest;
import com.ziio.example.model.RpcResponse;

import java.io.IOException;

/**
 * 静态代理
 */
public class UserServiceProxy implements UserService {

    @Override
    public User getUser(User user) {
        // 序列化器
        Serializer serializer = new JdkSerializer();

        //发请求
        RpcRequest rpcRequest = new RpcRequest();
        rpcRequest.setServiceName(UserService.class.getName());
        rpcRequest.setMethodName("getUser");
        rpcRequest.setParameterTypes(new Class[]{User.class});
        rpcRequest.setArgs(new Object[]{user});

        try {
            byte[] bodyBytes = serializer.serialize(rpcRequest);
            byte[] result;
            try(HttpResponse httpResponse = HttpRequest.post("http://localhost:8881")
                        .body(bodyBytes)
                        .execute())
            {
                result = httpResponse.bodyBytes();
            }
            // 反序列化 result
            RpcResponse rpcResponse = serializer.deserialize(result,RpcResponse.class);
            return (User) rpcResponse.getData();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
