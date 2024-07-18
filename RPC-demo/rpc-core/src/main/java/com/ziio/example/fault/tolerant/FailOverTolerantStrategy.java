package com.ziio.example.fault.tolerant;

import com.ziio.example.model.RpcResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * 转移到其他节点 --- 容错策略
 */
@Slf4j
public class FailOverTolerantStrategy implements TolerantStrategy {

    @Override
    public RpcResponse doTolerant(Map<String, Object> context, Exception e) {
        // todo : 转移到其他节点
        return null;
    }
}
