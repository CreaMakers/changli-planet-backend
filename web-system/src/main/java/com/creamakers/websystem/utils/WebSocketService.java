package com.creamakers.websystem.utils;


import com.creamakers.websystem.domain.vo.response.NotificationResp;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WebSocketService extends TextWebSocketHandler {

    // 用于存储连接的 WebSocket 会话，userId 为连接的唯一标识
    private final ConcurrentHashMap<Long, WebSocketSession> userSessions = new ConcurrentHashMap<>();

    // 连接时保存 session
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        Long userId = Long.valueOf(session.getUri().getPath().split("/")[2]); // 假设路径包含用户ID
        userSessions.put(userId, session);
    }

    // 断开时移除 session
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        Long userId = Long.valueOf(session.getUri().getPath().split("/")[2]);
        userSessions.remove(userId);
    }

    // 发送消息给特定用户
    public void sendMessageToUser(Long userId, NotificationResp notificationResp) {
        WebSocketSession session = userSessions.get(userId);
        if (session != null && session.isOpen()) {
            try {
                // 将 NotificationResp 对象转换为 JSON 字符串
                ObjectMapper objectMapper = new ObjectMapper();
                String message = objectMapper.writeValueAsString(notificationResp);
                session.sendMessage(new TextMessage(message));  // 发送消息
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
