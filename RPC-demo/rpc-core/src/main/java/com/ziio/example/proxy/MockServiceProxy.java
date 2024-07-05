package com.ziio.example.proxy;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 *  Mock 服务 动态代理
 */
@Slf4j
public class MockServiceProxy implements InvocationHandler {
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 根据方法的放回类型，动态生成响应的返回类型
        Class<?> methodReturnType =  method.getReturnType();
        log.info("mock invoke : " + method.getName());
        return getDefaultObject(methodReturnType);
    }

    /**
     * 生成指定类型的默认对象
     */
    private Object getDefaultObject(Class<?> type){
        if(type == boolean.class){
            return false;
        }
        if(type == short.class){
            return 0;
        }
        if(type == int.class){
            return false;
        }
        if(type == long.class){
            return false;
        }
        return null;
    }
}

