package com.plateforme_etudiant.demo.config;

import com.plateforme_etudiant.demo.model.Professeur;
import com.plateforme_etudiant.demo.model.Utilisateur;
import com.plateforme_etudiant.demo.model.enums.Role;
import com.plateforme_etudiant.demo.repository.ProfesseurRepository;
import com.plateforme_etudiant.demo.repository.UtilisateurRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private ProfesseurRepository professeurRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public void run(String... args) {
        log.info("=== VÉRIFICATION DES DONNÉES ===");

        // Créer un compte professeur de test
        if (!utilisateurRepository.existsByEmail("professeur@demo.com")) {
            log.info("📝 Création du compte professeur de test...");

            // Créer l'utilisateur avec mot de passe encodé
            Utilisateur utilisateur = new Utilisateur();
            utilisateur.setEmail("professeur@demo.com");
            utilisateur.setNomUtilisateur("profvalmont");
            utilisateur.setMotDePasse(passwordEncoder.encode("demo123")); // Encodé !
            utilisateur.setPrenom("Jean-Pierre");
            utilisateur.setNom("Valmont");
            utilisateur.setRole(Role.PROFESSEUR);
            utilisateur.setActif(true);
            utilisateur.setDateCreation(LocalDateTime.now());

            Utilisateur savedUser = utilisateurRepository.save(utilisateur);
            log.info("✅ Utilisateur créé: {} (ID: {})", savedUser.getEmail(), savedUser.getId());

            // Créer le professeur associé
            Professeur professeur = new Professeur();
            professeur.setUtilisateur(savedUser);
            professeur.setSpecialite("Mathématiques");
            professeur.setBiographie("Professeur expert en mathématiques");
            professeur.setVerifie(true);
            professeur.setDateCreation(LocalDateTime.now());

            professeurRepository.save(professeur);
            log.info("✅ Professeur créé avec succès");

        } else {
            log.info("ℹ️ Le compte professeur@demo.com existe déjà");

            // Vérifier l'association
            Utilisateur utilisateur = utilisateurRepository.findByEmail("professeur@demo.com").orElse(null);
            if (utilisateur != null && professeurRepository.findByUtilisateur(utilisateur).isEmpty()) {
                log.warn("⚠️ Création du professeur associé...");
                Professeur professeur = new Professeur();
                professeur.setUtilisateur(utilisateur);
                professeur.setSpecialite("Mathématiques");
                professeur.setBiographie("Professeur expert");
                professeur.setVerifie(true);
                professeur.setDateCreation(LocalDateTime.now());
                professeurRepository.save(professeur);
                log.info("✅ Professeur associé créé");
            }

            // Vérifier que le mot de passe est encodé
            if (utilisateur != null && utilisateur.getMotDePasse() != null && !utilisateur.getMotDePasse().startsWith("$2a$")) {
                log.warn("⚠️ Mise à jour du mot de passe en BCrypt...");
                utilisateur.setMotDePasse(passwordEncoder.encode("demo123"));
                utilisateurRepository.save(utilisateur);
                log.info("✅ Mot de passe encodé");
            }
        }

        log.info("=== INITIALISATION TERMINÉE ===");
        log.info("🔐 Compte de test: professeur@demo.com / demo123");
    }
}