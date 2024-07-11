package com.ziio.example.protocol;

import lombok.Getter;

/**
 * 消息类型枚举
 */
@Getter
public enum ProtocolMessageTypeEnum {
    REQUEST(0),
    RESPONSE(1),
    HEART_BEAT(2),
    OTHERS(3);

    private final int value;

    ProtocolMessageTypeEnum( int value){
        this.value = value;
    }

    /**
     * 根据 value 获取枚举
     */
    public static ProtocolMessageTypeEnum getEnumByValue(int value){
        for(ProtocolMessageTypeEnum anEnum : ProtocolMessageTypeEnum.values()){
            if(anEnum.value==value){
                return anEnum;
            }
        }
        return null;
    }
}
