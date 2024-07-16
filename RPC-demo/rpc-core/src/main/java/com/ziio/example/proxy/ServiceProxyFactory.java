package com.ziio.example.proxy;

import java.lang.reflect.Proxy;

/**
 * 动态代理工厂(用于创建代理对象)
 */
public class ServiceProxyFactory {

    /**
     * 根据服务接口创建代理对象
     * @param serviceClass
     * @return
     * @param <T>
     */
    public static  <T> T getProxy(Class<T> serviceClass){
        // 根据类信息 获取 代理对象
        return (T) Proxy.newProxyInstance(
                serviceClass.getClassLoader(),
                new Class[]{serviceClass},
                new TcpServiceProxy()
        );
    }

    /**
     * 根据服务接口获取mock对象
     */
    public static  <T> T getMockProxy(Class<T> serviceClass){
        // 根据类信息 获取 代理对象
        return (T) Proxy.newProxyInstance(
                serviceClass.getClassLoader(),
                new Class[]{serviceClass},
                new MockServiceProxy()
        );
    }
}
