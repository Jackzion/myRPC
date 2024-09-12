package com.ziio.example.protocol;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProtocolMessage<T> {
    /**
     * 消息头
     */
    private Header header ;

    /**
     * 消息体
     */
    private T body; // T : rpcRequest or rpcResponse

    @Data
    public static class Header{
        /**
         * 魔数 ， 保证安全性
         */
        private byte magic;
        /**
         * 协议版本号
         */
        private byte version;
        /**
         * 序列化器
         */
        private byte serializer ;
        /**
         * 消息类型 （request or response）
         */
        private byte type;
        /**
         * （响应）状态码
         */
        private byte status;
        /**
         * 请求 id
         */
        private long requestId;
        /**
         * 消息体长度
         */
        private int bodyLength;
    }
}
