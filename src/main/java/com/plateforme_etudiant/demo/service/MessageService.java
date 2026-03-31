package com.plateforme_etudiant.demo.service;

import com.plateforme_etudiant.demo.model.Conversation;
import com.plateforme_etudiant.demo.model.Message;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class MessageService {

    private final Map<String, List<Message>> messageStorage = new ConcurrentHashMap<>();
    private final Map<String, Conversation> conversationStorage = new ConcurrentHashMap<>();

    public Message saveMessage(Message message) {
        message.setId(UUID.randomUUID().toString());
        message.setTimestamp(LocalDateTime.now());
        message.setRead(false);

        String conversationId = generateConversationId(message.getSenderId(), message.getReceiverId());
        message.setConversationId(conversationId);

        messageStorage.computeIfAbsent(conversationId, k -> new ArrayList<>()).add(message);
        updateConversation(message);

        return message;
    }

    public List<Conversation> getUserConversations(String userId, String userName, String userRole) {
        return conversationStorage.values().stream()
                .filter(conv -> conv.getParticipant1Id().equals(userId) || conv.getParticipant2Id().equals(userId))
                .sorted((c1, c2) -> {
                    if (c1.getLastMessageTime() == null) return 1;
                    if (c2.getLastMessageTime() == null) return -1;
                    return c2.getLastMessageTime().compareTo(c1.getLastMessageTime());
                })
                .collect(Collectors.toList());
    }

    public List<Message> getConversationMessages(String conversationId) {
        return messageStorage.getOrDefault(conversationId, new ArrayList<>());
    }

    public void markMessagesAsRead(String conversationId, String userId) {
        List<Message> messages = messageStorage.get(conversationId);
        if (messages != null) {
            messages.forEach(msg -> {
                if (msg.getReceiverId() != null && msg.getReceiverId().equals(userId) && !msg.isRead()) {
                    msg.setRead(true);
                }
            });
        }

        Conversation conv = conversationStorage.get(conversationId);
        if (conv != null) {
            conv.setUnreadCount(countUnreadMessages(conversationId, userId));
        }
    }

    public int getUnreadCount(String userId) {
        return conversationStorage.values().stream()
                .filter(conv -> conv.getParticipant1Id().equals(userId) || conv.getParticipant2Id().equals(userId))
                .mapToInt(conv -> conv.getUnreadCount())
                .sum();
    }

    private void updateConversation(Message message) {
        String conversationId = message.getConversationId();
        Conversation conv = conversationStorage.get(conversationId);

        if (conv == null) {
            conv = new Conversation(
                    message.getSenderId(), message.getSenderName(), message.getSenderRole(),
                    message.getReceiverId(), message.getReceiverName(), message.getReceiverRole()
            );
            conversationStorage.put(conversationId, conv);
        }

        conv.setLastMessageTime(message.getTimestamp());
        String preview = message.getContent();
        if (preview != null && preview.length() > 50) {
            preview = preview.substring(0, 47) + "...";
        }
        conv.setLastMessagePreview(preview);

        if (message.getReceiverId() != null && !message.isRead()) {
            conv.setUnreadCount(countUnreadMessages(conversationId, message.getReceiverId()));
        }
    }

    private int countUnreadMessages(String conversationId, String userId) {
        List<Message> messages = messageStorage.get(conversationId);
        if (messages == null || userId == null) return 0;
        return (int) messages.stream()
                .filter(m -> m.getReceiverId() != null && m.getReceiverId().equals(userId) && !m.isRead())
                .count();
    }

    private String generateConversationId(String userId1, String userId2) {
        if (userId1 == null || userId2 == null) return "";
        List<String> ids = Arrays.asList(userId1, userId2);
        Collections.sort(ids);
        return ids.get(0) + "_" + ids.get(1);
    }
}