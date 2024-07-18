package com.ziio.example.fault.tolerant;

import com.ziio.example.serializer.SpiLoader;
import sun.security.jca.GetInstance;

/**
 * 容错策略工厂
 */
public class TolerantStrategyFactory {
    // SPI 加载
    static {
        SpiLoader.load(TolerantStrategy.class);
    }

    // 默认
    private static final TolerantStrategy DEFAULT_TOLERANT_STARTEGY = new FailFastTolerantStrategy();

    // 获得实例
    public static TolerantStrategy getInstance(String key){
        return SpiLoader.getInstance(TolerantStrategy.class,key);
    }
}
