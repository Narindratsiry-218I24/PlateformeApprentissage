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

import java.time.LocalDateTime;
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

    @Transactional
    public ProfesseurResponseDTO creerProfesseur(ProfesseurRequestDTO request) {
        log.info("Création d'un nouveau professeur: {}", request.getEmail());

        Utilisateur utilisateur = utilisateurService.creerUtilisateur(
                request.getNomUtilisateur(),
                request.getEmail(),
                request.getMotDePasse(),
                request.getPrenom(),
                request.getNom(),
                Role.PROFESSEUR
        );

        Professeur professeur = new Professeur();
        professeur.setUtilisateur(utilisateur);
        professeur.setSpecialite(request.getSpecialite());
        professeur.setBiographie(request.getBiographie());
        professeur.setVerifie(request.getVerifie() != null ? request.getVerifie() : false);
        professeur.setDateCreation(LocalDateTime.now());

        Professeur professeurSauvegarde = professeurRepository.save(professeur);
        log.info("Professeur créé avec succès, ID: {}", professeurSauvegarde.getId());

        return convertirEnResponse(professeurSauvegarde);
    }

    @Transactional(readOnly = true)
    public ProfesseurResponseDTO getProfesseurById(Long id) {
        Professeur professeur = professeurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Professeur non trouvé avec l'ID: " + id));
        return convertirEnResponse(professeur);
    }

    @Transactional(readOnly = true)
    public ProfesseurResponseDTO getProfesseurByEmail(String email) {
        Utilisateur utilisateur = utilisateurService.findByEmail(email);
        Professeur professeur = professeurRepository.findByUtilisateur(utilisateur)
                .orElseThrow(() -> new RuntimeException("Professeur non trouvé pour l'email: " + email));
        return convertirEnResponse(professeur);
    }

    @Transactional(readOnly = true)
    public ProfesseurResponseDTO getProfesseurByUtilisateurId(Long utilisateurId) {
        Professeur professeur = professeurRepository.findByUtilisateurId(utilisateurId)
                .orElseThrow(() -> new RuntimeException("Professeur non trouvé pour l'utilisateur ID: " + utilisateurId));
        return convertirEnResponse(professeur);
    }

    @Transactional(readOnly = true)
    public List<ProfesseurResponseDTO> getAllProfesseurs() {
        return professeurRepository.findAll()
                .stream()
                .map(this::convertirEnResponse)
                .collect(Collectors.toList());
    }

    private ProfesseurResponseDTO convertirEnResponse(Professeur professeur) {
        ProfesseurResponseDTO dto = new ProfesseurResponseDTO();
        dto.setId(professeur.getId());

        Utilisateur utilisateur = professeur.getUtilisateur();
        dto.setUtilisateurId(utilisateur.getId());
        dto.setNomUtilisateur(utilisateur.getNomUtilisateur());
        dto.setEmail(utilisateur.getEmail());
        dto.setPrenom(utilisateur.getPrenom());
        dto.setNom(utilisateur.getNom());
        dto.setNomComplet(utilisateur.getPrenom() + " " + utilisateur.getNom());
        dto.setSpecialite(professeur.getSpecialite());
        dto.setBiographie(professeur.getBiographie());
        dto.setVerifie(professeur.getVerifie());
        dto.setDateCreation(professeur.getDateCreation());

        long nombreCours = professeurRepository.countCoursByProfesseurId(professeur.getId());
        dto.setNombreCours(nombreCours);

        return dto;
    }
}