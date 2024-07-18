package com.ziio.example.fault.tolerant;

import com.ziio.example.model.RpcResponse;

import java.util.Map;

/**
 * 快速失败 --- 容错策略
 */
public class FailFastTolerantStrategy implements TolerantStrategy {

    @Override
    public RpcResponse doTolerant(Map<String, Object> context, Exception e) {
        throw new RuntimeException("服务报错",e);
    }
}
