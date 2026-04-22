package com.plateforme_etudiant.demo.service;

import com.plateforme_etudiant.demo.dto.ProgressionCoursDTO;
import com.plateforme_etudiant.demo.model.ContenuItem;
import com.plateforme_etudiant.demo.model.Cours;
import com.plateforme_etudiant.demo.model.Inscription;
import com.plateforme_etudiant.demo.model.Progression;
import com.plateforme_etudiant.demo.model.Utilisateur;
import com.plateforme_etudiant.demo.repository.ContenuItemRepository;
import com.plateforme_etudiant.demo.repository.CoursRepository;
import com.plateforme_etudiant.demo.repository.InscriptionRepository;
import com.plateforme_etudiant.demo.repository.ProgressionRepository;
import com.plateforme_etudiant.demo.repository.UtilisateurRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ProgressionService {

    private static final Logger log = LoggerFactory.getLogger(ProgressionService.class);

    private final ProgressionRepository progressionRepository;
    private final ContenuItemRepository contenuItemRepository;
    private final InscriptionRepository inscriptionRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final CoursRepository coursRepository;

    public ProgressionService(ProgressionRepository progressionRepository,
                              ContenuItemRepository contenuItemRepository,
                              InscriptionRepository inscriptionRepository,
                              UtilisateurRepository utilisateurRepository,
                              CoursRepository coursRepository) {
        this.progressionRepository = progressionRepository;
        this.contenuItemRepository = contenuItemRepository;
        this.inscriptionRepository = inscriptionRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.coursRepository = coursRepository;
    }

    @Transactional(readOnly = true)
    public int calculerProgressionCours(Long etudiantId, Long coursId) {
        Long totalContenus = contenuItemRepository.countByCoursId(coursId);
        if (totalContenus == null || totalContenus == 0) {
            return 0;
        }

        Long contenusCompletes = progressionRepository.countCompletedByApprenantAndCours(etudiantId, coursId);
        long completes = contenusCompletes == null ? 0L : contenusCompletes;

        return (int) Math.round((completes * 100.0) / totalContenus);
    }

    @Transactional(readOnly = true)
    public List<ProgressionCoursDTO> getProgressionParCours(Long etudiantId) {
        List<Inscription> inscriptions = inscriptionRepository.findByApprenantIdWithCours(etudiantId);
        List<ProgressionCoursDTO> resultats = new ArrayList<>();

        for (Inscription inscription : inscriptions) {
            Cours cours = inscription.getCours();
            if (cours == null) {
                continue;
            }

            Long totalContenus = contenuItemRepository.countByCoursId(cours.getId());
            Long contenusCompletes = progressionRepository.countCompletedByApprenantAndCours(etudiantId, cours.getId());
            int progression = calculerProgressionCours(etudiantId, cours.getId());

            ProgressionCoursDTO dto = new ProgressionCoursDTO();
            dto.setCoursId(cours.getId());
            dto.setCoursTitre(cours.getTitre());
            dto.setCoursDescriptionCourte(cours.getDescriptionCourte());
            dto.setImageCouverture(cours.getImageCouverture());
            dto.setProgression(progression);
            dto.setContenusTotal(totalContenus == null ? 0 : totalContenus.intValue());
            dto.setContenusCompletes(contenusCompletes == null ? 0 : contenusCompletes.intValue());
            dto.setDateDernierAcces(inscription.getDateDernierAcces());
            dto.setEstTermine(progression >= 100);
            resultats.add(dto);
        }

        return resultats;
    }

    @Transactional
    public int completerContenu(Long etudiantId, Long coursId, Long contenuId) {
        verifierInscription(etudiantId, coursId);

        ContenuItem contenu = contenuItemRepository.findById(contenuId)
                .orElseThrow(() -> new RuntimeException("Contenu non trouvé"));

        Long coursDuContenu = contenu.getSection() != null && contenu.getSection().getCours() != null
                ? contenu.getSection().getCours().getId()
                : null;

        if (coursDuContenu == null || !coursDuContenu.equals(coursId)) {
            throw new RuntimeException("Ce contenu n'appartient pas au cours demandé");
        }

        Progression progression = progressionRepository.findByApprenantIdAndContenuItemId(etudiantId, contenuId)
                .orElseGet(() -> creerProgressionInitiale(etudiantId, contenu));

        if (!Boolean.TRUE.equals(progression.getEstComplete())) {
            progression.setEstComplete(true);
            progression.setDateCompletion(LocalDateTime.now());
        }

        progression.setDateDerniereActivite(LocalDateTime.now());
        progressionRepository.save(progression);

        Inscription inscription = inscriptionRepository.findByApprenantIdAndCoursId(etudiantId, coursId)
                .orElseThrow(() -> new RuntimeException("Inscription introuvable"));
        inscription.setDateDernierAcces(LocalDateTime.now());

        int progressionCours = calculerProgressionCours(etudiantId, coursId);
        inscription.setTermine(progressionCours >= 100);
        inscriptionRepository.save(inscription);

        log.info("Contenu {} complété par l'étudiant {} pour le cours {}", contenuId, etudiantId, coursId);
        return progressionCours;
    }

    @Transactional(readOnly = true)
    public void verifierInscription(Long etudiantId, Long coursId) {
        if (!inscriptionRepository.existsByApprenantIdAndCoursId(etudiantId, coursId)) {
            throw new RuntimeException("L'étudiant n'est pas inscrit à ce cours");
        }
    }

    private Progression creerProgressionInitiale(Long etudiantId, ContenuItem contenu) {
        Utilisateur etudiant = utilisateurRepository.findById(etudiantId)
                .orElseThrow(() -> new RuntimeException("Étudiant non trouvé"));

        Progression progression = new Progression();
        progression.setApprenant(etudiant);
        progression.setContenuItem(contenu);
        progression.setEstComplete(false);
        progression.setTempsPasse(0);
        progression.setDernierePosition(0);
        progression.setDateDebut(LocalDateTime.now());
        return progression;
    }

    @Transactional(readOnly = true)
    public boolean coursExiste(Long coursId) {
        return coursRepository.existsById(coursId);
    }
}
