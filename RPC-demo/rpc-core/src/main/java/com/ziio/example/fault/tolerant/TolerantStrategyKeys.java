package com.ziio.example.fault.tolerant;

/**
 * 容错策略键名
 */
public interface TolerantStrategyKeys {
    /**
     * 故障恢复
     */
    String FAIL_BACK = "failBack"; // todo: Mock , 执行本地方法 and 服务

    /**
     * 快速失败
     */
    String FAIL_FAST = "failFast";

    /**
     * 故障转移
     */
    String FAIL_OVER = "failOver";  // todo: 选择其他节点发送请求

    /**
     * 静默处理
     */
    String FAIL_SAFE = "failSafe";
}
