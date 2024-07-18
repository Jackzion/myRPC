package com.ziio.example.fault.tolerant;

import com.ziio.example.model.RpcResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * 降级处理 --- 容错策略
 */
@Slf4j
public class FailBackTolerantStrategy implements TolerantStrategy {

    @Override
    public RpcResponse doTolerant(Map<String, Object> context, Exception e) {
        // todo : 降级处理
        return null;
    }
}
