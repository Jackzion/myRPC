package com.example.rpcstarter.annotation;

import com.example.rpcstarter.bootstrap.RpcConsumerBootstrap;
import com.example.rpcstarter.bootstrap.RpcInitBootstrap;
import com.example.rpcstarter.bootstrap.RpcProviderBootstrap;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 启动 RPC 注解
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Import({RpcInitBootstrap.class, RpcProviderBootstrap.class, RpcConsumerBootstrap.class})
public @interface EnableRpc {
    /**
     * 需要启动 server , todo: @target 是否为 field？
     * @return
     */
    boolean needServer() default true;
}
