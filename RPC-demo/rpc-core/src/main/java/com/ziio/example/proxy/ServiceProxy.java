package com.ziio.example.proxy;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.ziio.example.RpcApplication;
import com.ziio.example.model.RpcRequest;
import com.ziio.example.model.RpcResponse;
import com.ziio.example.serializer.JdkSerializer;
import com.ziio.example.serializer.Serializer;
import javafx.application.Application;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * 动态代理 , InvocationHandler实现
 */
public class ServiceProxy implements InvocationHandler {

    // proxy 动态代理对象每执行每个方法都会经过 invoke , 即userService 每个接口都会得到增强实现
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 序列化器
        Serializer serializer = new JdkSerializer();

        //发请求
        RpcRequest rpcRequest = new RpcRequest();
        rpcRequest.setServiceName(method.getDeclaringClass().getName());
        rpcRequest.setMethodName(method.getName());
        rpcRequest.setParameterTypes(method.getParameterTypes());
        rpcRequest.setArgs(args);

        try {
            // 請求序列化
            byte[] bodyBytes = serializer.serialize(rpcRequest);
            byte[] result;
            System.out.println();
            System.out.println("http://localhost:" + RpcApplication.getRpcConfig().getServerPort());
            // 发送 http' 请求
            try(HttpResponse httpResponse = HttpRequest.post("http://localhost:" + RpcApplication.getRpcConfig().getServerPort())
                    .body(bodyBytes)
                    .execute())
            {
                result = httpResponse.bodyBytes();
            }
            // 反序列化 result
            RpcResponse rpcResponse = serializer.deserialize(result,RpcResponse.class);
            return  rpcResponse.getData();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
