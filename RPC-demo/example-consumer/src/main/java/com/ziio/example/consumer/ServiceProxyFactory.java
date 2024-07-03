package com.ziio.example.consumer;

import java.lang.reflect.Proxy;

/**
 * 动态代理工厂(用于创建代理对象)
 */
public class ServiceProxyFactory {
    public static  <T> T getProxy(Class<T> serviceClass){
        // 根据类信息 获取 代理对象
        return (T) Proxy.newProxyInstance(
                serviceClass.getClassLoader(),
                new Class[]{serviceClass},
                new ServiceProxy()
        );
    }
}
