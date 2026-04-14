package com.plateforme_etudiant.demo.controller.web;

import com.plateforme_etudiant.demo.model.Professeur;
import com.plateforme_etudiant.demo.model.Utilisateur;
import com.plateforme_etudiant.demo.model.enums.Role;
import com.plateforme_etudiant.demo.repository.ProfesseurRepository;
import com.plateforme_etudiant.demo.repository.UtilisateurRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;

@Controller
public class InscriptionController {

    private static final Logger log = LoggerFactory.getLogger(InscriptionController.class);

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private ProfesseurRepository professeurRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/inscription")
    public String inscriptionPage() {
        return "inscription";
    }

    @PostMapping("/inscription")
    public String inscription(@RequestParam String prenom,
                              @RequestParam String nom,
                              @RequestParam String email,
                              @RequestParam String nomUtilisateur,
                              @RequestParam String motDePasse,
                              @RequestParam String confirmMotDePasse,
                              @RequestParam(required = false) String role,  // NOUVEAU: rôle choisi
                              @RequestParam(required = false) String specialite,
                              @RequestParam(required = false) String biographie,
                              Model model) {

        log.info("=== TENTATIVE D'INSCRIPTION ===");
        log.info("Email: {}", email);
        log.info("Prénom: {}", prenom);
        log.info("Nom: {}", nom);
        log.info("Rôle choisi: {}", role);

        // 1. Vérifier que les mots de passe correspondent
        if (!motDePasse.equals(confirmMotDePasse)) {
            log.warn("❌ Les mots de passe ne correspondent pas");
            model.addAttribute("error", "Les mots de passe ne correspondent pas");
            return "inscription";
        }

        // 2. Vérifier la longueur du mot de passe
        if (motDePasse.length() < 6) {
            log.warn("❌ Mot de passe trop court");
            model.addAttribute("error", "Le mot de passe doit contenir au moins 6 caractères");
            return "inscription";
        }

        // 3. Vérifier si l'email existe déjà
        if (utilisateurRepository.existsByEmail(email)) {
            log.warn("❌ Email déjà utilisé: {}", email);
            model.addAttribute("error", "Cet email est déjà utilisé");
            return "inscription";
        }

        // 4. Vérifier si le nom d'utilisateur existe déjà
        if (utilisateurRepository.existsByNomUtilisateur(nomUtilisateur)) {
            log.warn("❌ Nom d'utilisateur déjà pris: {}", nomUtilisateur);
            model.addAttribute("error", "Ce nom d'utilisateur est déjà pris");
            return "inscription";
        }

        try {
            // 5. Déterminer le rôle (par défaut APPRENANT)
            Role userRole;
            if ("PROFESSEUR".equalsIgnoreCase(role)) {
                userRole = Role.PROFESSEUR;
            } else {
                userRole = Role.APPRENANT;  // Par défaut: étudiant
            }

            // 6. Créer l'utilisateur avec mot de passe ENCODÉ
            Utilisateur utilisateur = new Utilisateur();
            utilisateur.setEmail(email);
            utilisateur.setNomUtilisateur(nomUtilisateur);
            utilisateur.setMotDePasse(passwordEncoder.encode(motDePasse));
            utilisateur.setPrenom(prenom);
            utilisateur.setNom(nom);
            utilisateur.setRole(userRole);
            utilisateur.setActif(true);
            utilisateur.setDateCreation(LocalDateTime.now());

            Utilisateur savedUser = utilisateurRepository.save(utilisateur);
            log.info("✅ Utilisateur créé avec ID: {} et rôle: {}", savedUser.getId(), userRole);

            // 7. Si c'est un professeur, créer l'entité Professeur associée
            if (userRole == Role.PROFESSEUR) {
                Professeur professeur = new Professeur();
                professeur.setUtilisateur(savedUser);
                professeur.setSpecialite(specialite != null && !specialite.isEmpty() ? specialite : "Non spécifiée");
                professeur.setBiographie(biographie != null ? biographie : "");
                professeur.setVerifie(true);
                professeur.setDateCreation(LocalDateTime.now());

                professeurRepository.save(professeur);
                log.info("✅ Professeur créé avec succès");
                model.addAttribute("success", "✅ Inscription professeur réussie ! Vous pouvez maintenant vous connecter.");
            } else {
                log.info("✅ Étudiant créé avec succès");
                model.addAttribute("success", "✅ Inscription étudiante réussie ! Vous pouvez maintenant vous connecter.");
            }

        } catch (Exception e) {
            log.error("❌ Erreur lors de l'inscription: {}", e.getMessage(), e);
            model.addAttribute("error", "Erreur: " + e.getMessage());
            return "inscription";
        }

        return "inscription";
    }
}