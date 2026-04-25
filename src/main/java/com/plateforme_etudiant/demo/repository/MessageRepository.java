package com.plateforme_etudiant.demo.repository;

import com.plateforme_etudiant.demo.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, String> {
    List<Message> findByConversationIdOrderByTimestampAsc(String conversationId);
    int countByReceiverIdAndReadFalse(String receiverId);
}
