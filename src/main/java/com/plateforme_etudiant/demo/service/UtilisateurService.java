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

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UtilisateurService {

    private static final Logger log = LoggerFactory.getLogger(UtilisateurService.class);

    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;

    public UtilisateurService(UtilisateurRepository utilisateurRepository) {
        this.utilisateurRepository = utilisateurRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    @Transactional
    public Utilisateur creerUtilisateur(String nomUtilisateur, String email, String motDePasse,
                                        String prenom, String nom, Role role) {
        log.info("Création d'un nouvel utilisateur: {}", email);

        if (utilisateurRepository.existsByEmail(email)) {
            throw new RuntimeException("Un utilisateur avec cet email existe déjà");
        }

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
        utilisateur.setDateCreation(LocalDateTime.now());

        return utilisateurRepository.save(utilisateur);
    }

    public Utilisateur findByEmail(String email) {
        return utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'email: " + email));
    }

    public boolean verifierMotDePasse(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    public Utilisateur findByNomUtilisateur(String nomUtilisateur) {
        return utilisateurRepository.findByNomUtilisateur(nomUtilisateur)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec le pseudo: " + nomUtilisateur));
    }

    public Utilisateur findById(Long id) {
        return utilisateurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'ID: " + id));
    }

    public boolean emailExiste(String email) {
        return utilisateurRepository.existsByEmail(email);
    }

    public List<Utilisateur> findAllApprenants() {
        return utilisateurRepository.findByRole(Role.APPRENANT);
    }

    public List<Utilisateur> findAllProfesseurs() {
        return utilisateurRepository.findByRole(Role.PROFESSEUR);
    }

    @Transactional
    public Utilisateur updateUtilisateur(Long id, String prenom, String nom, String email, Boolean actif) {
        Utilisateur utilisateur = findById(id);
        utilisateur.setPrenom(prenom);
        utilisateur.setNom(nom);
        utilisateur.setEmail(email);
        utilisateur.setActif(actif);
        return utilisateurRepository.save(utilisateur);
    }

    @Transactional
    public void deleteUtilisateur(Long id) {
        Utilisateur utilisateur = findById(id);
        utilisateurRepository.delete(utilisateur);
    }

    public long countAllApprenants() {
        return utilisateurRepository.findByRole(Role.APPRENANT).size();
    }

    public long countAllProfesseurs() {
        return utilisateurRepository.findByRole(Role.PROFESSEUR).size();
    }

    @Transactional
    public void mettreAJourActivite(Long utilisateurId, Long coursActifId) {
        Utilisateur u = findById(utilisateurId);
        u.setDerniereActivite(LocalDateTime.now());
        if (coursActifId != null) {
            u.setCoursActifId(coursActifId);
        }
        utilisateurRepository.save(u);
    }

    public List<Utilisateur> getEtudiantsEnLigne(int minutesInactivite) {
        LocalDateTime threshold = LocalDateTime.now().minusMinutes(minutesInactivite);
        return utilisateurRepository.findActiveApprenantsSince(threshold);
    }
}