package com.ziio.example.fault.tolerant;

import com.ziio.example.model.RpcResponse;

import java.util.Map;

/**
 * 容错策略
 */
public interface TolerantStrategy {
    /**
     * 容错
     * @param context 上下文用于传递数据
     * @param e 异常触发
     * @return rpcResponse
     */
    RpcResponse doTolerant(Map<String,Object> context , Exception e);
}
