package com.plateforme_etudiant.demo.controller.web;

import com.plateforme_etudiant.demo.config.WebSocketEventListener;
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
import org.springframework.web.multipart.MultipartFile;
import com.plateforme_etudiant.demo.service.FileUploadService;

import jakarta.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import org.springframework.http.ResponseEntity;
import com.plateforme_etudiant.demo.config.WebSocketEventListener;

@Controller
@RequestMapping("/messages")
public class MessageWebController {

    private final MessageService messageService;
    private final UtilisateurService utilisateurService;
    private final UtilisateurRepository utilisateurRepository;
    private final FileUploadService fileUploadService;

    public MessageWebController(MessageService messageService,
                                UtilisateurService utilisateurService,
                                UtilisateurRepository utilisateurRepository,
                                FileUploadService fileUploadService) {
        this.messageService = messageService;
        this.utilisateurService = utilisateurService;
        this.utilisateurRepository = utilisateurRepository;
        this.fileUploadService = fileUploadService;
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
        model.addAttribute("role", utilisateur.getRole().name().toLowerCase());

        // Charger la liste des interlocuteurs possibles
        List<Utilisateur> interlocuteurs;
        if (utilisateur.estApprenant()) {
            // Pour un étudiant : uniquement les professeurs de ses cours
            interlocuteurs = utilisateurRepository.findProfesseursByApprenantId(utilisateur.getId());
        } else {
            // Pour les autres (Prof/Admin) : tout le monde sauf soi-même
            interlocuteurs = utilisateurRepository.findAll();
            interlocuteurs.removeIf(u -> u.getId().equals(utilisateur.getId()));
        }
        model.addAttribute("interlocuteurs", interlocuteurs);

        if (utilisateur.estProfesseur()) {
            return "professeur/messages/liste";
        } else {
            return "etudiant/messages";
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

        // Si la conversation n'existe pas, rediriger vers la liste des messages
        if (currentConversation == null) {
            return "redirect:/messages";
        }

        model.addAttribute("conversations", conversations);
        model.addAttribute("messages", messages);
        model.addAttribute("currentConversationId", conversationId);
        model.addAttribute("currentConversation", currentConversation);
        model.addAttribute("userId", utilisateur.getId().toString());
        model.addAttribute("role", utilisateur.getRole().name().toLowerCase());

        // Charger la liste des interlocuteurs possibles
        List<Utilisateur> interlocuteurs;
        if (utilisateur.estApprenant()) {
            interlocuteurs = utilisateurRepository.findProfesseursByApprenantId(utilisateur.getId());
        } else {
            interlocuteurs = utilisateurRepository.findAll();
            interlocuteurs.removeIf(u -> u.getId().equals(utilisateur.getId()));
        }
        model.addAttribute("interlocuteurs", interlocuteurs);

        if (utilisateur.estProfesseur()) {
            return "professeur/messages/liste";
        } else {
            return "etudiant/messages";
        }
    }

    // ─── Envoyer un message dans une conversation existante ───────────────────
    @PostMapping("/{conversationId}/envoyer")
    public String envoyerMessage(@PathVariable String conversationId,
                                 @RequestParam String receiverId,
                                 @RequestParam String receiverName,
                                 @RequestParam String receiverRole,
                                 @RequestParam(required = false) String content,
                                 @RequestParam(value = "file", required = false) MultipartFile file,
                                 HttpSession session, RedirectAttributes redirectAttributes) {

        Utilisateur utilisateur = getUtilisateurFromSession(session);
        if (utilisateur == null) return "redirect:/login";

        if ((content == null || content.trim().isEmpty()) && (file == null || file.isEmpty())) {
            redirectAttributes.addFlashAttribute("error", "Le message ne peut pas être vide.");
            return "redirect:/messages/" + conversationId;
        }

        Message message = new Message();
        if (file != null && !file.isEmpty()) {
            try {
                String fileUrl = fileUploadService.uploadMessageFile(file);
                message.setFileUrl(fileUrl);
                message.setFileName(file.getOriginalFilename());
                if (content == null || content.trim().isEmpty()) {
                    content = "Fichier envoyé : " + file.getOriginalFilename();
                }
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("error", "Erreur lors de l'envoi du fichier.");
                return "redirect:/messages/" + conversationId;
            }
        }
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
                                       @RequestParam(required = false) String content,
                                       @RequestParam(value = "file", required = false) MultipartFile file,
                                       HttpSession session,
                                       RedirectAttributes redirectAttributes) {

        Utilisateur expediteur = getUtilisateurFromSession(session);
        if (expediteur == null) return "redirect:/login";

        if ((content == null || content.trim().isEmpty()) && (file == null || file.isEmpty())) {
            redirectAttributes.addFlashAttribute("error", "Le message ne peut pas être vide.");
            return "redirect:/messages";
        }

        try {
            Utilisateur destinataire = utilisateurService.findById(destinataireId);

            Message message = new Message();
            if (file != null && !file.isEmpty()) {
                String fileUrl = fileUploadService.uploadMessageFile(file);
                message.setFileUrl(fileUrl);
                message.setFileName(file.getOriginalFilename());
                if (content == null || content.trim().isEmpty()) {
                    content = "Fichier envoyé : " + file.getOriginalFilename();
                }
            }
            
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

    @GetMapping("/online-users")
    @ResponseBody
    public List<String> onlineUsers(HttpSession session) {
        if (getUtilisateurFromSession(session) == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(WebSocketEventListener.getOnlineUsers());
    }
}
