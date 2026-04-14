package com.plateforme_etudiant.demo.service;

import com.plateforme_etudiant.demo.dto.*;
import com.plateforme_etudiant.demo.model.*;
import com.plateforme_etudiant.demo.model.enums.Role;
import com.plateforme_etudiant.demo.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.time.LocalDateTime;
import java.util.Optional;


@Service
public class EtudiantService {

    private static final Logger log = LoggerFactory.getLogger(EtudiantService.class);

    private final UtilisateurRepository utilisateurRepository;
    private final InscriptionRepository inscriptionRepository;
    private final ProgressionRepository progressionRepository;
    private final CoursRepository coursRepository;
    private final ChapitreRepository chapitreRepository;
    private final ContenuItemRepository contenuItemRepository;

    public EtudiantService(UtilisateurRepository utilisateurRepository,
                           InscriptionRepository inscriptionRepository,
                           ProgressionRepository progressionRepository,
                           CoursRepository coursRepository,
                           ChapitreRepository chapitreRepository,
                           ContenuItemRepository contenuItemRepository) {
        this.utilisateurRepository = utilisateurRepository;
        this.inscriptionRepository = inscriptionRepository;
        this.progressionRepository = progressionRepository;
        this.coursRepository = coursRepository;
        this.chapitreRepository = chapitreRepository;
        this.contenuItemRepository = contenuItemRepository;
    }

    @Transactional(readOnly = true)
    public EtudiantResponseDTO getEtudiantById(Long utilisateurId) {
        Utilisateur utilisateur = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'ID: " + utilisateurId));

        if (utilisateur.getRole() != Role.APPRENANT) {
            throw new RuntimeException("L'utilisateur n'est pas un étudiant");
        }

        return convertirEnResponse(utilisateur);
    }

    @Transactional(readOnly = true)
    public List<CoursEtudiantDTO> getCoursInscrits(Long utilisateurId) {
        List<Inscription> inscriptions = inscriptionRepository.findByApprenantIdWithCours(utilisateurId);

        List<CoursEtudiantDTO> result = new ArrayList<>();

        for (Inscription inscription : inscriptions) {
            Cours cours = inscription.getCours();
            if (cours != null) {
                CoursEtudiantDTO dto = new CoursEtudiantDTO();
                dto.setId(cours.getId());
                dto.setTitre(cours.getTitre());
                dto.setDescriptionCourte(cours.getDescriptionCourte());
                dto.setImageCouverture(cours.getImageCouverture());
                dto.setPublie(cours.getPublie());
                dto.setDureeEstimee(cours.getDureeEstimee());

                if (cours.getProfesseur() != null && cours.getProfesseur().getUtilisateur() != null) {
                    dto.setProfesseur(cours.getProfesseur().getUtilisateur().getNomComplet());
                }

                Integer progression = calculerProgressionCours(utilisateurId, cours.getId());
                dto.setProgression(progression);
                dto.setDerniereActivite(inscription.getDateDernierAcces());

                result.add(dto);
            }
        }

        return result;
    }

    @Transactional(readOnly = true)
    public List<CoursEtudiantDTO> getCoursRecommandes(Long utilisateurId) {
        List<Long> coursInscritsIds = inscriptionRepository.findCoursIdsByApprenantId(utilisateurId);

        List<Cours> coursRecommandes;
        if (coursInscritsIds.isEmpty()) {
            coursRecommandes = coursRepository.findByPublieTrueOrderByDateCreationDesc();
        } else {
            coursRecommandes = coursRepository.findByPublieTrueOrderByDateCreationDesc()
                    .stream()
                    .filter(c -> !coursInscritsIds.contains(c.getId()))
                    .limit(6)
                    .collect(Collectors.toList());
        }

        List<CoursEtudiantDTO> result = new ArrayList<>();
        for (Cours cours : coursRecommandes) {
            CoursEtudiantDTO dto = new CoursEtudiantDTO();
            dto.setId(cours.getId());
            dto.setTitre(cours.getTitre());
            dto.setDescriptionCourte(cours.getDescriptionCourte());
            dto.setImageCouverture(cours.getImageCouverture());
            dto.setDureeEstimee(cours.getDureeEstimee());

            if (cours.getProfesseur() != null && cours.getProfesseur().getUtilisateur() != null) {
                dto.setProfesseur(cours.getProfesseur().getUtilisateur().getNomComplet());
            }

            result.add(dto);
        }

        return result;
    }

    @Transactional(readOnly = true)
    public StatistiquesEtudiantDTO getStatistiques(Long utilisateurId) {
        List<CoursEtudiantDTO> coursInscrits = getCoursInscrits(utilisateurId);

        int totalCours = coursInscrits.size();
        int totalHeures = coursInscrits.stream()
                .mapToInt(c -> c.getDureeEstimee() != null ? c.getDureeEstimee() / 60 : 0)
                .sum();

        double progressionMoyenne = coursInscrits.stream()
                .mapToInt(c -> c.getProgression() != null ? c.getProgression() : 0)
                .average()
                .orElse(0);

        int certificats = (int) coursInscrits.stream()
                .filter(c -> c.getProgression() != null && c.getProgression() >= 100)
                .count();

        return new StatistiquesEtudiantDTO(
                totalCours,
                totalHeures,
                (int) Math.round(progressionMoyenne),
                certificats
        );
    }

    @Transactional(readOnly = true)
    public List<ProgressionMatiereDTO> getProgressionsParMatiere(Long utilisateurId) {
        List<CoursEtudiantDTO> coursInscrits = getCoursInscrits(utilisateurId);

        Map<String, List<Integer>> matieresProgressions = new HashMap<>();

        for (CoursEtudiantDTO cours : coursInscrits) {
            Cours coursComplet = coursRepository.findById(cours.getId()).orElse(null);
            String nomMatiere = "Autres";
            String description = "";

            if (coursComplet != null && coursComplet.getCategorie() != null) {
                nomMatiere = coursComplet.getCategorie().getNom();
                description = coursComplet.getCategorie().getDescription();
            }

            matieresProgressions.computeIfAbsent(nomMatiere, k -> new ArrayList<>())
                    .add(cours.getProgression() != null ? cours.getProgression() : 0);
        }

        List<ProgressionMatiereDTO> result = new ArrayList<>();
        for (Map.Entry<String, List<Integer>> entry : matieresProgressions.entrySet()) {
            double moyenne = entry.getValue().stream().mapToInt(Integer::intValue).average().orElse(0);
            ProgressionMatiereDTO dto = new ProgressionMatiereDTO();
            dto.setNom(entry.getKey());
            dto.setDescription("Progression moyenne en " + entry.getKey());
            dto.setProgression((int) Math.round(moyenne));
            result.add(dto);
        }

        return result;
    }

    @Transactional
    public void inscrireCours(Long utilisateurId, Long coursId) {
        if (inscriptionRepository.existsByApprenantIdAndCoursId(utilisateurId, coursId)) {
            throw new RuntimeException("Déjà inscrit à ce cours");
        }

        Utilisateur etudiant = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new RuntimeException("Étudiant non trouvé"));

        Cours cours = coursRepository.findById(coursId)
                .orElseThrow(() -> new RuntimeException("Cours non trouvé"));

        Inscription inscription = new Inscription();
        inscription.setApprenant(etudiant);
        inscription.setCours(cours);
        inscription.setDateInscription(LocalDateTime.now());
        inscription.setDateDernierAcces(LocalDateTime.now());
        inscription.setTermine(false);
        inscription.setFavori(false);

        inscriptionRepository.save(inscription);

        cours.incrementerApprenants();
        coursRepository.save(cours);

        log.info("Étudiant {} inscrit au cours {}", utilisateurId, coursId);
    }

    @Transactional(readOnly = true)
    public Integer calculerProgressionCours(Long utilisateurId, Long coursId) {
        try {
            Long totalContenus = contenuItemRepository.countByCoursId(coursId);

            if (totalContenus == null || totalContenus == 0) {
                return 0;
            }

            Long contenusCompletes = progressionRepository.countCompletedByApprenantAndCours(utilisateurId, coursId);

            if (contenusCompletes == null) {
                return 0;
            }

            return (int) Math.round((contenusCompletes * 100.0) / totalContenus);

        } catch (Exception e) {
            log.error("Erreur lors du calcul de progression pour le cours {}: {}", coursId, e.getMessage());
            return 0;
        }
    }

    private EtudiantResponseDTO convertirEnResponse(Utilisateur utilisateur) {
        EtudiantResponseDTO dto = new EtudiantResponseDTO();
        dto.setId(utilisateur.getId());
        dto.setUtilisateurId(utilisateur.getId());
        dto.setNomUtilisateur(utilisateur.getNomUtilisateur());
        dto.setEmail(utilisateur.getEmail());
        dto.setPrenom(utilisateur.getPrenom());
        dto.setNom(utilisateur.getNom());
        dto.setNomComplet(utilisateur.getNomComplet());
        dto.setDateCreation(utilisateur.getDateCreation());

        long nombreCours = inscriptionRepository.countByApprenantId(utilisateur.getId());
        dto.setNombreCoursInscrits((int) nombreCours);

        if (nombreCours < 3) {
            dto.setNiveau("Débutant");
        } else if (nombreCours < 8) {
            dto.setNiveau("Intermédiaire");
        } else {
            dto.setNiveau("Avancé");
        }

        return dto;
    }

    @Transactional
    public void completerContenu(Long etudiantId, Long contenuId) {
        log.info("Complétion du contenu {} par l'étudiant {}", contenuId, etudiantId);

        try {
            // Vérifier si la progression existe déjà
            Optional<Progression> existing = progressionRepository.findByApprenantIdAndContenuItemId(etudiantId, contenuId);

            if (existing.isPresent()) {
                Progression progression = existing.get();
                if (!progression.getEstComplete()) {
                    progression.setEstComplete(true);
                    progression.setDateCompletion(LocalDateTime.now());
                    progressionRepository.save(progression);
                    log.info("✅ Contenu {} marqué comme complété", contenuId);
                }
            } else {
                // Créer une nouvelle progression
                Utilisateur etudiant = utilisateurRepository.findById(etudiantId)
                        .orElseThrow(() -> new RuntimeException("Étudiant non trouvé"));
                ContenuItem contenu = contenuItemRepository.findById(contenuId)
                        .orElseThrow(() -> new RuntimeException("Contenu non trouvé"));

                Progression progression = new Progression();
                progression.setApprenant(etudiant);
                progression.setContenuItem(contenu);
                progression.setEstComplete(true);
                progression.setDateDebut(LocalDateTime.now());
                progression.setDateCompletion(LocalDateTime.now());
                progression.setTempsPasse(0);
                progression.setDernierePosition(0);
                progressionRepository.save(progression);
                log.info("✅ Nouvelle progression créée pour le contenu {}", contenuId);
            }
        } catch (Exception e) {
            log.error("❌ Erreur lors de la complétion: {}", e.getMessage());
            throw new RuntimeException("Erreur lors de la mise à jour de la progression: " + e.getMessage());
        }
    }
}