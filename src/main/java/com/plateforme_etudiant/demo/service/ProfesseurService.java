package com.plateforme_etudiant.demo.service;

import com.plateforme_etudiant.demo.dto.ProfesseurRequestDTO;
import com.plateforme_etudiant.demo.dto.ProfesseurResponseDTO;
import com.plateforme_etudiant.demo.model.Professeur;
import com.plateforme_etudiant.demo.model.Utilisateur;
import com.plateforme_etudiant.demo.model.enums.Role;
import com.plateforme_etudiant.demo.repository.ProfesseurRepository;
import com.plateforme_etudiant.demo.repository.UtilisateurRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProfesseurService {

    private static final Logger log = LoggerFactory.getLogger(ProfesseurService.class);

    private final ProfesseurRepository professeurRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final UtilisateurService utilisateurService;

    public ProfesseurService(ProfesseurRepository professeurRepository,
                             UtilisateurRepository utilisateurRepository,
                             UtilisateurService utilisateurService) {
        this.professeurRepository = professeurRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.utilisateurService = utilisateurService;
    }

    /**
     * Créer un professeur avec son utilisateur
     */
    @Transactional
    public ProfesseurResponseDTO creerProfesseur(ProfesseurRequestDTO request) {
        log.info("Création d'un nouveau professeur: {}", request.getEmail());

        // 1. Créer l'utilisateur avec le rôle PROFESSEUR
        Utilisateur utilisateur = utilisateurService.creerUtilisateur(
                request.getNomUtilisateur(),
                request.getEmail(),
                request.getMotDePasse(),
                request.getPrenom(),
                request.getNom(),
                Role.PROFESSEUR
        );

        // 2. Créer le professeur associé
        Professeur professeur = new Professeur();
        professeur.setUtilisateur(utilisateur);
        professeur.setSpecialite(request.getSpecialite());
        professeur.setBiographie(request.getBiographie());
        professeur.setVerifie(request.getVerifie() != null ? request.getVerifie() : false);

        Professeur professeurSauvegarde = professeurRepository.save(professeur);
        log.info("Professeur créé avec succès, ID: {}", professeurSauvegarde.getId());

        return convertirEnResponse(professeurSauvegarde);
    }

    /**
     * Récupérer tous les professeurs
     */
    @Transactional(readOnly = true)
    public List<ProfesseurResponseDTO> getAllProfesseurs() {
        log.debug("Récupération de tous les professeurs");
        return professeurRepository.findAll()
                .stream()
                .map(this::convertirEnResponse)
                .collect(Collectors.toList());
    }

    /**
     * Récupérer un professeur par ID
     */
    @Transactional(readOnly = true)
    public ProfesseurResponseDTO getProfesseurById(Long id) {
        log.debug("Récupération du professeur par ID: {}", id);
        Professeur professeur = professeurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Professeur non trouvé avec l'ID: " + id));
        return convertirEnResponse(professeur);
    }

    /**
     * Récupérer un professeur par ID utilisateur
     */
    @Transactional(readOnly = true)
    public ProfesseurResponseDTO getProfesseurByUtilisateurId(Long utilisateurId) {
        log.debug("Récupération du professeur par ID utilisateur: {}", utilisateurId);
        Professeur professeur = professeurRepository.findByUtilisateurId(utilisateurId)
                .orElseThrow(() -> new RuntimeException("Professeur non trouvé pour l'utilisateur ID: " + utilisateurId));
        return convertirEnResponse(professeur);
    }

    /**
     * Mettre à jour un professeur
     */
    @Transactional
    public ProfesseurResponseDTO mettreAJourProfesseur(Long id, ProfesseurRequestDTO request) {
        log.info("Mise à jour du professeur ID: {}", id);

        Professeur professeur = professeurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Professeur non trouvé avec l'ID: " + id));

        Utilisateur utilisateur = professeur.getUtilisateur();

        // Mettre à jour les informations utilisateur
        utilisateur.setNomUtilisateur(request.getNomUtilisateur());
        utilisateur.setEmail(request.getEmail());
        utilisateur.setPrenom(request.getPrenom());
        utilisateur.setNom(request.getNom());

        if (request.getMotDePasse() != null && !request.getMotDePasse().isEmpty()) {
            // Ici il faudrait encoder le mot de passe
            utilisateur.setMotDePasse(request.getMotDePasse());
        }

        utilisateurRepository.save(utilisateur);

        // Mettre à jour les informations professeur
        professeur.setSpecialite(request.getSpecialite());
        professeur.setBiographie(request.getBiographie());
        professeur.setVerifie(request.getVerifie() != null ? request.getVerifie() : professeur.getVerifie());

        Professeur professeurMisAJour = professeurRepository.save(professeur);
        log.info("Professeur mis à jour, ID: {}", id);

        return convertirEnResponse(professeurMisAJour);
    }

    /**
     * Supprimer un professeur (supprime aussi l'utilisateur associé)
     */
    @Transactional
    public void supprimerProfesseur(Long id) {
        log.info("Suppression du professeur ID: {}", id);

        Professeur professeur = professeurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Professeur non trouvé avec l'ID: " + id));

        Long utilisateurId = professeur.getUtilisateur().getId();

        // Supprimer le professeur
        professeurRepository.delete(professeur);

        // Supprimer l'utilisateur associé
        utilisateurRepository.deleteById(utilisateurId);

        log.info("Professeur et utilisateur supprimés, ID professeur: {}, ID utilisateur: {}", id, utilisateurId);
    }

    /**
     * Vérifier un professeur
     */
    @Transactional
    public ProfesseurResponseDTO verifierProfesseur(Long id, boolean verifie) {
        log.info("Vérification du professeur ID: {}, vérifié: {}", id, verifie);

        Professeur professeur = professeurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Professeur non trouvé avec l'ID: " + id));

        professeur.setVerifie(verifie);
        Professeur professeurMisAJour = professeurRepository.save(professeur);

        return convertirEnResponse(professeurMisAJour);
    }

    /**
     * Convertir un professeur en DTO
     */
    private ProfesseurResponseDTO convertirEnResponse(Professeur professeur) {
        ProfesseurResponseDTO dto = new ProfesseurResponseDTO();
        dto.setId(professeur.getId());

        Utilisateur utilisateur = professeur.getUtilisateur();
        dto.setUtilisateurId(utilisateur.getId());
        dto.setNomUtilisateur(utilisateur.getNomUtilisateur());
        dto.setEmail(utilisateur.getEmail());
        dto.setPrenom(utilisateur.getPrenom());
        dto.setNom(utilisateur.getNom());
        dto.setNomComplet(utilisateur.getNomComplet());

        dto.setSpecialite(professeur.getSpecialite());
        dto.setBiographie(professeur.getBiographie());
        dto.setVerifie(professeur.getVerifie());
        dto.setDateCreation(professeur.getDateCreation());

        // Compter le nombre de cours
        long nombreCours = professeurRepository.countCoursByProfesseurId(professeur.getId());
        dto.setNombreCours(nombreCours);

        return dto;
    }
}