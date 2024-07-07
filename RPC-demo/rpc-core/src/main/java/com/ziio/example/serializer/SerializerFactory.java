package com.ziio.example.serializer;

import java.util.HashMap;
import java.util.Map;

/**
 * 序列化工厂 + 单例 -- 复用
 */
public class SerializerFactory {

    /**
     * spi 加载 serializer 接口类信息
     */
    static {
        SpiLoader.load(Serializer.class);
    }

    /**
     * 默认序列化器
     */
    private static final Serializer DEFAULT_SERIALIZER = new JdkSerializer();

    /**
     * 从 spi 注册器获得 serializer 实例类型
     * @param key
     * @return
     */
    public static Serializer getInstance(String key){
        return SpiLoader.getInstance(Serializer.class,key);
    }


}
