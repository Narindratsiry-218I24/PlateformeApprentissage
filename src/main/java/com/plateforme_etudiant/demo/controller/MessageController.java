package com.plateforme_etudiant.demo.controller;

import com.plateforme_etudiant.demo.model.Conversation;
import com.plateforme_etudiant.demo.model.Message;
import com.plateforme_etudiant.demo.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/professeur/messages")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @GetMapping
    public String messagesPage(Model model) {
        try {
            // Mock current user (remplacez par l'utilisateur connecté plus tard)
            String userId = "prof_1";
            String userName = "Prof. Martin Dupont";
            String userRole = "PROFESSEUR";

            List<Conversation> conversations = messageService.getUserConversations(userId, userName, userRole);
            int unreadCount = messageService.getUnreadCount(userId);

            model.addAttribute("conversations", conversations);
            model.addAttribute("unreadMessagesCount", unreadCount);
            model.addAttribute("currentPage", "messages");
            model.addAttribute("pageTitle", "Messages - Professeur");

            // Ajout du professeur pour le header
            Map<String, Object> professeur = new HashMap<>();
            professeur.put("prenom", "Martin");
            professeur.put("nom", "Dupont");
            professeur.put("specialite", "Mathématiques");
            model.addAttribute("professeur", professeur);

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Erreur lors du chargement des messages: " + e.getMessage());
            model.addAttribute("conversations", new ArrayList<>());
            model.addAttribute("unreadMessagesCount", 0);
        }

        return "professeur/messages/liste";
    }

    @GetMapping("/{userId}")
    @ResponseBody
    public List<Message> getConversation(@PathVariable String userId) {
        try {
            String currentUserId = "prof_1";
            String conversationId = generateConversationId(currentUserId, userId);
            messageService.markMessagesAsRead(conversationId, currentUserId);
            return messageService.getConversationMessages(conversationId);
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private String generateConversationId(String user1, String user2) {
        if (user1 == null || user2 == null) return "";
        return user1.compareTo(user2) < 0 ? user1 + "_" + user2 : user2 + "_" + user1;
    }
}