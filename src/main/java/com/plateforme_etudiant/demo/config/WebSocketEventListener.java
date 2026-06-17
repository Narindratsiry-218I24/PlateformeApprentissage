package com.plateforme_etudiant.demo.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Set;

@Component
public class WebSocketEventListener {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketEventListener.class);

    // userId -> connection count
    private static final ConcurrentHashMap<String, Integer> onlineUsers = new ConcurrentHashMap<>();

    private final SimpMessagingTemplate messagingTemplate;

    public WebSocketEventListener(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public static Set<String> getOnlineUsers() {
        return onlineUsers.keySet();
    }

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String userId = getUserId(headerAccessor);
        if (userId != null) {
            int count = onlineUsers.getOrDefault(userId, 0);
            onlineUsers.put(userId, count + 1);
            if (count == 0) {
                logger.info("User Online: " + userId);
            }
            messagingTemplate.convertAndSend("/topic/online-users", onlineUsers.keySet());
        }
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String userId = getUserId(headerAccessor);
        if (userId != null) {
            int count = onlineUsers.getOrDefault(userId, 0);
            if (count <= 1) {
                onlineUsers.remove(userId);
                logger.info("User Offline: " + userId);
                messagingTemplate.convertAndSend("/topic/online-users", onlineUsers.keySet());
            } else {
                onlineUsers.put(userId, count - 1);
            }
        }
    }

    private String getUserId(StompHeaderAccessor accessor) {
        if (accessor.getSessionAttributes() != null) {
            Object userIdObj = accessor.getSessionAttributes().get("userId");
            if (userIdObj != null) {
                return userIdObj.toString();
            }
        }
        return null;
    }
}
