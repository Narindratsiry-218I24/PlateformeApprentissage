package com.plateforme_etudiant.demo.controller.web;

import com.plateforme_etudiant.demo.model.Professeur;
import com.plateforme_etudiant.demo.model.Utilisateur;
import com.plateforme_etudiant.demo.model.enums.Role;
import com.plateforme_etudiant.demo.repository.ProfesseurRepository;
import com.plateforme_etudiant.demo.repository.UtilisateurRepository;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthWebController {

    private static final Logger log = LoggerFactory.getLogger(AuthWebController.class);

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private ProfesseurRepository professeurRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/login")
    public String loginPage(@RequestParam(required = false) String error,
                            @RequestParam(required = false) String logout,
                            Model model) {
        log.info("=== PAGE LOGIN AFFICHÉE ===");
        if (error != null) {
            model.addAttribute("error", "Email ou mot de passe incorrect");
        }
        if (logout != null) {
            model.addAttribute("success", "Vous avez été déconnecté");
        }
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String email,
                        @RequestParam String password,
                        HttpSession session,
                        Model model) {

        log.info("=== TENTATIVE DE CONNEXION ===");
        log.info("Email: {}", email);

        try {
            Utilisateur utilisateur = utilisateurRepository.findByEmail(email).orElse(null);

            if (utilisateur == null) {
                log.warn("❌ Utilisateur non trouvé: {}", email);
                model.addAttribute("error", "Email ou mot de passe incorrect");
                return "login";
            }

            log.info("✅ Utilisateur trouvé: {}", utilisateur.getEmail());
            log.info("Rôle: {}", utilisateur.getRole());

            boolean passwordMatch = passwordEncoder.matches(password, utilisateur.getMotDePasse());

            if (!passwordMatch) {
                log.warn("❌ Mot de passe incorrect pour: {}", email);
                model.addAttribute("error", "Email ou mot de passe incorrect");
                return "login";
            }

            log.info("✅ Mot de passe correct");

            if (!utilisateur.getActif()) {
                log.warn("❌ Compte inactif: {}", email);
                model.addAttribute("error", "Votre compte est désactivé");
                return "login";
            }

            session.setAttribute("userId", utilisateur.getId());
            session.setAttribute("userEmail", utilisateur.getEmail());
            session.setAttribute("userNom", utilisateur.getPrenom() + " " + utilisateur.getNom());
            session.setAttribute("userRole", utilisateur.getRole().toString());

            if (utilisateur.getRole() == Role.PROFESSEUR) {
                Professeur professeur = professeurRepository.findByUtilisateur(utilisateur).orElse(null);

                if (professeur == null) {
                    log.error("❌ Professeur non trouvé pour: {}", email);
                    model.addAttribute("error", "Compte professeur non configuré");
                    return "login";
                }

                session.setAttribute("professeurId", professeur.getId());
                log.info("✅ Connexion professeur réussie ! Redirection vers /professeur/dashboard");
                return "redirect:/professeur/dashboard";

            } else if (utilisateur.getRole() == Role.APPRENANT) {
                log.info("✅ Connexion étudiant réussie ! Redirection vers /etudiant/dashboard");
                return "redirect:/etudiant/dashboard";

            } else {
                log.warn("❌ Rôle non géré: {}", utilisateur.getRole());
                model.addAttribute("error", "Rôle non supporté");
                return "login";
            }

        } catch (Exception e) {
            log.error("❌ Erreur lors de la connexion: {}", e.getMessage(), e);
            model.addAttribute("error", "Erreur technique: " + e.getMessage());
            return "login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login?logout=true";
    }
}