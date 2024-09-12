package com.example.rpcstarter.annotation;

import com.ziio.example.constant.RpcConstant;
import com.ziio.example.fault.retry.RetryStrategyKeys;
import com.ziio.example.fault.tolerant.TolerantStrategyKeys;
import com.ziio.example.loadbalancer.LoadBalancerKeys;
import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 服务提供者注解
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Component // 组合注解，注解同时变为 bean service
public @interface RpcService {
    /**
     * 服务接口类
     */
    Class<?> interfaceClass() default void.class;

    /**
     * 版本
     */
    String serviceVersion() default RpcConstant.DEFAULT_SERVICE_VERSION;

}
