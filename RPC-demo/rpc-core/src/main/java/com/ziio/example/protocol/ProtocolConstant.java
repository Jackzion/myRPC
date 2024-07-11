package com.ziio.example.protocol;

/**
 * 协议常量
 * 记录了和自定义协议有关的关键信息，比如消息头长度、魔数、版本号。
 */
public interface ProtocolConstant {
    /**
     * 消息头长度
     */
    int MESSAGE_HEADER_LENGTH = 17;
    /**
     * 魔数
     */
    byte PROTOCOL_MAGIC = 0x1;
    /**
     * 协议版本号
     */
    byte PROTOCOL_VERSION = 0x1;
}
