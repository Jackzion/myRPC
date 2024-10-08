package com.ziio.example.registry;

import com.ziio.example.serializer.SpiLoader;

/**
 * 注册中心工厂（用于获取注册中心对象）
 */
public class RegistryFactory {

    // 载入 注册中心配置
    static {
        SpiLoader.load(Registry.class);
    }

    /**
     * 默认注册中心
     */
    private static final Registry DEFAULT_REGISTRY = new EtcdRegistry();

    /**
     * 获取 registry 实例
     */
    public static Registry getInstance(String key){
        return SpiLoader.getInstance(Registry.class,key);
    }
}
