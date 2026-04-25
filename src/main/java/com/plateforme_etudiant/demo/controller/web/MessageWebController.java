package com.plateforme_etudiant.demo.controller.web;

import com.plateforme_etudiant.demo.model.Conversation;
import com.plateforme_etudiant.demo.model.Message;
import com.plateforme_etudiant.demo.model.Utilisateur;
import com.plateforme_etudiant.demo.model.enums.Role;
import com.plateforme_etudiant.demo.repository.UtilisateurRepository;
import com.plateforme_etudiant.demo.service.MessageService;
import com.plateforme_etudiant.demo.service.UtilisateurService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.Objects;

@Controller
@RequestMapping("/messages")
public class MessageWebController {

    private final MessageService messageService;
    private final UtilisateurService utilisateurService;
    private final UtilisateurRepository utilisateurRepository;

    public MessageWebController(MessageService messageService,
                                UtilisateurService utilisateurService,
                                UtilisateurRepository utilisateurRepository) {
        this.messageService = messageService;
        this.utilisateurService = utilisateurService;
        this.utilisateurRepository = utilisateurRepository;
    }

    // ─── Résolution de l'utilisateur courant depuis la session ───────────────
    private Utilisateur getUtilisateurFromSession(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return null;
        try {
            return utilisateurService.findById(userId);
        } catch (Exception e) {
            return null;
        }
    }

    // ─── Liste des conversations ──────────────────────────────────────────────
    @GetMapping
    public String index(Model model, HttpSession session) {
        Utilisateur utilisateur = getUtilisateurFromSession(session);
        if (utilisateur == null) return "redirect:/login";

        List<Conversation> conversations = messageService.getUserConversations(
                utilisateur.getId().toString(), utilisateur.getNomComplet(), utilisateur.getRole().name()
        );

        model.addAttribute("conversations", conversations);
        model.addAttribute("userId", utilisateur.getId().toString());

        // Charger la liste des interlocuteurs possibles (selon rôle)
        if (utilisateur.estProfesseur()) {
            // Le prof peut écrire à ses étudiants
            List<Utilisateur> etudiants = utilisateurRepository.findByRole(Role.APPRENANT);
            model.addAttribute("interlocuteurs", etudiants);
            return "professeur/messages/liste";
        } else {
            // L'étudiant peut écrire aux professeurs
            List<Utilisateur> professeurs = utilisateurRepository.findByRole(Role.PROFESSEUR);
            model.addAttribute("interlocuteurs", professeurs);
            return "Etudiant/messages";
        }
    }

    // ─── Détail d'une conversation ────────────────────────────────────────────
    @GetMapping("/{conversationId}")
    public String detail(@PathVariable String conversationId, Model model, HttpSession session) {
        Utilisateur utilisateur = getUtilisateurFromSession(session);
        if (utilisateur == null) return "redirect:/login";

        messageService.markMessagesAsRead(conversationId, utilisateur.getId().toString());

        List<Conversation> conversations = messageService.getUserConversations(
                utilisateur.getId().toString(), utilisateur.getNomComplet(), utilisateur.getRole().name()
        );
        List<Message> messages = messageService.getConversationMessages(conversationId);
        Conversation currentConversation = conversations.stream()
                .filter(conversation -> Objects.equals(conversation.getId(), conversationId))
                .findFirst()
                .orElse(null);

        model.addAttribute("conversations", conversations);
        model.addAttribute("messages", messages);
        model.addAttribute("currentConversationId", conversationId);
        model.addAttribute("currentConversation", currentConversation);
        model.addAttribute("userId", utilisateur.getId().toString());

        if (utilisateur.estProfesseur()) {
            List<Utilisateur> etudiants = utilisateurRepository.findByRole(Role.APPRENANT);
            model.addAttribute("interlocuteurs", etudiants);
            return "professeur/messages/liste";
        } else {
            List<Utilisateur> professeurs = utilisateurRepository.findByRole(Role.PROFESSEUR);
            model.addAttribute("interlocuteurs", professeurs);
            return "Etudiant/messages";
        }
    }

    // ─── Envoyer un message dans une conversation existante ───────────────────
    @PostMapping("/{conversationId}/envoyer")
    public String envoyerMessage(@PathVariable String conversationId,
                                 @RequestParam String receiverId,
                                 @RequestParam String receiverName,
                                 @RequestParam String receiverRole,
                                 @RequestParam String content,
                                 HttpSession session, RedirectAttributes redirectAttributes) {

        Utilisateur utilisateur = getUtilisateurFromSession(session);
        if (utilisateur == null) return "redirect:/login";

        if (content == null || content.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Le message ne peut pas être vide.");
            return "redirect:/messages/" + conversationId;
        }

        Message message = new Message();
        message.setSenderId(utilisateur.getId().toString());
        message.setSenderName(utilisateur.getNomComplet());
        message.setSenderRole(utilisateur.getRole().name());

        message.setReceiverId(receiverId);
        message.setReceiverName(receiverName);
        message.setReceiverRole(receiverRole);
        message.setContent(content.trim());

        messageService.saveMessage(message);

        return "redirect:/messages/" + conversationId;
    }

    // ─── Initier une nouvelle conversation ────────────────────────────────────
    @PostMapping("/nouvelle")
    public String nouvelleConversation(@RequestParam Long destinataireId,
                                       @RequestParam String content,
                                       HttpSession session,
                                       RedirectAttributes redirectAttributes) {

        Utilisateur expediteur = getUtilisateurFromSession(session);
        if (expediteur == null) return "redirect:/login";

        if (content == null || content.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Le message ne peut pas être vide.");
            return "redirect:/messages";
        }

        try {
            Utilisateur destinataire = utilisateurService.findById(destinataireId);

            Message message = new Message();
            message.setSenderId(expediteur.getId().toString());
            message.setSenderName(expediteur.getNomComplet());
            message.setSenderRole(expediteur.getRole().name());
            message.setReceiverId(destinataire.getId().toString());
            message.setReceiverName(destinataire.getNomComplet());
            message.setReceiverRole(destinataire.getRole().name());
            message.setContent(content.trim());

            Message saved = messageService.saveMessage(message);
            String convId = saved.getConversation().getId();

            return "redirect:/messages/" + convId;

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur : " + e.getMessage());
            return "redirect:/messages";
        }
    }
}
