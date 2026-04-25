package com.plateforme_etudiant.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "conversation")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Conversation {
    @Id
    private String id;
    
    private String participant1Id;
    private String participant1Name;
    private String participant1Role;
    private String participant2Id;
    private String participant2Name;
    private String participant2Role;
    
    @OneToMany(mappedBy = "conversation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Message> messages = new ArrayList<>();
    
    private LocalDateTime lastMessageTime;
    private String lastMessagePreview;
    private int unreadCount = 0;

    public Conversation(String participant1Id, String participant1Name, String participant1Role,
                        String participant2Id, String participant2Name, String participant2Role) {
        this.id = participant1Id + "_" + participant2Id;
        this.participant1Id = participant1Id;
        this.participant1Name = participant1Name;
        this.participant1Role = participant1Role;
        this.participant2Id = participant2Id;
        this.participant2Name = participant2Name;
        this.participant2Role = participant2Role;
    }

    public String getOtherParticipantId(String userId) {
        if (participant1Id != null && participant1Id.equals(userId)) return participant2Id;
        return participant1Id;
    }

    public String getOtherParticipantName(String userId) {
        if (participant1Id != null && participant1Id.equals(userId)) return participant2Name;
        return participant1Name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getParticipant1Id() {
        return participant1Id;
    }

    public void setParticipant1Id(String participant1Id) {
        this.participant1Id = participant1Id;
    }

    public String getParticipant1Name() {
        return participant1Name;
    }

    public void setParticipant1Name(String participant1Name) {
        this.participant1Name = participant1Name;
    }

    public String getParticipant1Role() {
        return participant1Role;
    }

    public void setParticipant1Role(String participant1Role) {
        this.participant1Role = participant1Role;
    }

    public String getParticipant2Id() {
        return participant2Id;
    }

    public void setParticipant2Id(String participant2Id) {
        this.participant2Id = participant2Id;
    }

    public String getParticipant2Name() {
        return participant2Name;
    }

    public void setParticipant2Name(String participant2Name) {
        this.participant2Name = participant2Name;
    }

    public String getParticipant2Role() {
        return participant2Role;
    }

    public void setParticipant2Role(String participant2Role) {
        this.participant2Role = participant2Role;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public LocalDateTime getLastMessageTime() {
        return lastMessageTime;
    }

    public void setLastMessageTime(LocalDateTime lastMessageTime) {
        this.lastMessageTime = lastMessageTime;
    }

    public String getLastMessagePreview() {
        return lastMessagePreview;
    }

    public void setLastMessagePreview(String lastMessagePreview) {
        this.lastMessagePreview = lastMessagePreview;
    }

    public int getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(int unreadCount) {
        this.unreadCount = unreadCount;
    }
}
