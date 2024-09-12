package com.example.rpcstarter.bootstrap;

import com.example.rpcstarter.annotation.RpcReference;
import com.ziio.example.proxy.ServiceProxyFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.lang.reflect.Field;

/**
 * rpc 服务消费者启动
 * 让启动类实现 BeanPostProcessor 接口的 postProcessAfterInitialization 方法，
 * 就可以在某个服务提供者 Bean 初始化后，执行注册服务等操作了
 */

public class RpcConsumerBootstrap implements BeanPostProcessor {

    /**
     * bean 实例化后执行
     * @param bean
     * @param beanName
     * @return
     * @throws BeansException
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        // 反射 获得 bean class信息
        Class<?> beanClass = bean.getClass();
        // 遍历获得对象所有属性
        Field[] declaredFields = beanClass.getDeclaredFields();
        // 从属性中获取注解信息
        for(Field field:declaredFields){
            RpcReference rpcReference = field.getAnnotation(RpcReference.class);
            // 默认值处理，获取 bean 上的接口信息
            if(rpcReference!=null){
                // 为属性生成代理对象
                Class<?> interfaceClass = rpcReference.interfaceClass();
                // 默认值处理
                if(interfaceClass==void.class){
                    interfaceClass = field.getType();
                }
                field.setAccessible(true);
                Object proxy = ServiceProxyFactory.getProxy(interfaceClass);
                try {
                    // 将代理对象注入到属性中 , 6666 !
                    field.set(bean,proxy);
                    field.setAccessible(false);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("为字段注入代理对象失败！",e);
                }
            }
        }

        // 调用默认实现
        return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
    }
}
