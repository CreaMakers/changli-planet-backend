package com.hayaizo.chatsystem.common.strategy;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 消息处理器工厂
 */
@Component
public class MsgHandlerFactory {
    private static final Map<Integer,AbstractMsgHandler> STRATEGY_MAP = new ConcurrentHashMap<>();

    public static void register(Integer code,AbstractMsgHandler strategy){
        STRATEGY_MAP.put(code,strategy);
    }

    public static AbstractMsgHandler get(Integer code){
        AbstractMsgHandler abstractMsgHandler = STRATEGY_MAP.get(code);
        return abstractMsgHandler;
    }
}
