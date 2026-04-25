package com.plateforme_etudiant.demo.service;

import com.plateforme_etudiant.demo.model.Conversation;
import com.plateforme_etudiant.demo.model.Message;
import com.plateforme_etudiant.demo.repository.ConversationRepository;
import com.plateforme_etudiant.demo.repository.MessageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final ConversationRepository conversationRepository;

    public MessageService(MessageRepository messageRepository, ConversationRepository conversationRepository) {
        this.messageRepository = messageRepository;
        this.conversationRepository = conversationRepository;
    }

    @Transactional
    public Message saveMessage(Message message) {
        message.setId(UUID.randomUUID().toString());
        message.setTimestamp(LocalDateTime.now());
        message.setRead(false);

        String conversationId = generateConversationId(message.getSenderId(), message.getReceiverId());
        
        Conversation conv = conversationRepository.findById(conversationId).orElse(null);
        if (conv == null) {
            conv = new Conversation(
                    message.getSenderId(), message.getSenderName(), message.getSenderRole(),
                    message.getReceiverId(), message.getReceiverName(), message.getReceiverRole()
            );
            conv.setId(conversationId);
        }

        conv.setLastMessageTime(message.getTimestamp());
        String preview = message.getContent();
        if (preview != null && preview.length() > 50) {
            preview = preview.substring(0, 47) + "...";
        }
        conv.setLastMessagePreview(preview);
        
        conversationRepository.save(conv);

        message.setConversation(conv);
        return messageRepository.save(message);
    }

    @Transactional(readOnly = true)
    public List<Conversation> getUserConversations(String userId, String userName, String userRole) {
        return conversationRepository.findUserConversations(userId);
    }

    @Transactional(readOnly = true)
    public List<Message> getConversationMessages(String conversationId) {
        return messageRepository.findByConversationIdOrderByTimestampAsc(conversationId);
    }

    @Transactional
    public void markMessagesAsRead(String conversationId, String userId) {
        List<Message> messages = getConversationMessages(conversationId);
        boolean changed = false;
        for (Message msg : messages) {
            if (msg.getReceiverId() != null && msg.getReceiverId().equals(userId) && !msg.isRead()) {
                msg.setRead(true);
                changed = true;
            }
        }
        if (changed) {
            messageRepository.saveAll(messages);
            
            // Update unread count is now dynamic or can be recalculated
            Conversation conv = conversationRepository.findById(conversationId).orElse(null);
            if(conv != null) {
                // To keep it simple, we don't strictly need to store unread count in DB if we calculate it,
                // but let's recalculate and save just in case.
                int unread = (int) messages.stream()
                        .filter(m -> m.getReceiverId() != null && m.getReceiverId().equals(userId) && !m.isRead())
                        .count();
                conv.setUnreadCount(unread);
                conversationRepository.save(conv);
            }
        }
    }

    @Transactional(readOnly = true)
    public int getUnreadCount(String userId) {
        return messageRepository.countByReceiverIdAndReadFalse(userId);
    }

    private String generateConversationId(String userId1, String userId2) {
        if (userId1 == null || userId2 == null) return "";
        List<String> ids = Arrays.asList(userId1, userId2);
        Collections.sort(ids);
        return ids.get(0) + "_" + ids.get(1);
    }
}