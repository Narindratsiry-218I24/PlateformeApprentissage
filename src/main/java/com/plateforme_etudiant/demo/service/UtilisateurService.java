// UtilisateurService.java
package com.plateforme_etudiant.demo.service;

import com.plateforme_etudiant.demo.model.Utilisateur;
import com.plateforme_etudiant.demo.model.enums.Role;
import com.plateforme_etudiant.demo.repository.UtilisateurRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UtilisateurService {

    private static final Logger log = LoggerFactory.getLogger(UtilisateurService.class);

    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;

    public UtilisateurService(UtilisateurRepository utilisateurRepository) {
        this.utilisateurRepository = utilisateurRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    /**
     * Créer un utilisateur
     */
    @Transactional
    public Utilisateur creerUtilisateur(String nomUtilisateur, String email, String motDePasse,
                                        String prenom, String nom, Role role) {
        log.info("Création d'un nouvel utilisateur: {}", email);

        // Vérifier si l'email existe déjà
        if (utilisateurRepository.existsByEmail(email)) {
            throw new RuntimeException("Un utilisateur avec cet email existe déjà");
        }

        // Vérifier si le nom d'utilisateur existe déjà
        if (utilisateurRepository.existsByNomUtilisateur(nomUtilisateur)) {
            throw new RuntimeException("Ce nom d'utilisateur est déjà pris");
        }

        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setNomUtilisateur(nomUtilisateur);
        utilisateur.setEmail(email);
        utilisateur.setMotDePasse(passwordEncoder.encode(motDePasse));
        utilisateur.setPrenom(prenom);
        utilisateur.setNom(nom);
        utilisateur.setRole(role);
        utilisateur.setActif(true);

        return utilisateurRepository.save(utilisateur);
    }

    /**
     * Trouver un utilisateur par ID
     */
    public Utilisateur findById(Long id) {
        log.debug("Recherche de l'utilisateur par ID: {}", id);
        return utilisateurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'ID: " + id));
    }

    /**
     * Trouver un utilisateur par email
     */
    public Utilisateur findByEmail(String email) {
        log.debug("Recherche de l'utilisateur par email: {}", email);
        return utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'email: " + email));
    }

    /**
     * Vérifier si un email existe
     */
    public boolean emailExiste(String email) {
        return utilisateurRepository.existsByEmail(email);
    }
}