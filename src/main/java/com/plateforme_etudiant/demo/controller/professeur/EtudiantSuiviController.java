// EtudiantSuiviController.java
package com.plateforme_etudiant.demo.controller.professeur;

import com.plateforme_etudiant.demo.dto.*;
import com.plateforme_etudiant.demo.model.*;
import com.plateforme_etudiant.demo.repository.*;
import com.plateforme_etudiant.demo.service.ProgressionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/professeur/etudiants")
public class EtudiantSuiviController {

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private ProfesseurRepository professeurRepository;

    @Autowired
    private CoursRepository coursRepository;

    @Autowired
    private InscriptionRepository inscriptionRepository;

    @Autowired
    private ProgressionRepository progressionRepository;

    @Autowired
    private ContenuItemRepository contenuItemRepository;

    // Liste des étudiants du professeur
    @GetMapping
    public String listeEtudiants(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        try {
            Utilisateur utilisateur = utilisateurRepository.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

            Professeur professeur = professeurRepository.findByUtilisateur(utilisateur)
                    .orElseThrow(() -> new RuntimeException("Professeur non trouvé"));

            // Récupérer tous les cours du professeur
            List<Cours> coursList = coursRepository.findByProfesseur(professeur);
            List<Long> coursIds = coursList.stream().map(Cours::getId).collect(Collectors.toList());

            // Récupérer tous les étudiants inscrits à ces cours (sans doublons)
            Set<Utilisateur> etudiantsSet = new HashSet<>();
            for (Cours cours : coursList) {
                List<Inscription> inscriptions = inscriptionRepository.findByCoursIdWithApprenant(cours.getId());
                for (Inscription inscription : inscriptions) {
                    etudiantsSet.add(inscription.getApprenant());
                }
            }

            List<Utilisateur> etudiants = new ArrayList<>(etudiantsSet);

            // Construire les DTOs avec les statistiques
            List<EtudiantSuiviDTO> etudiantsDTO = new ArrayList<>();
            for (Utilisateur etudiant : etudiants) {
                EtudiantSuiviDTO dto = new EtudiantSuiviDTO();
                dto.setId(etudiant.getId());
                dto.setNomComplet(etudiant.getNomComplet());
                dto.setEmail(etudiant.getEmail());
                dto.setNomUtilisateur(etudiant.getNomUtilisateur());

                // Calculer le nombre de cours suivis
                long nbCours = inscriptionRepository.countByApprenantId(etudiant.getId());
                dto.setNombreCours((int) nbCours);

                // Calculer la progression moyenne sur tous les cours
                double progressionMoyenne = calculerProgressionMoyenne(etudiant.getId(), coursIds);
                dto.setProgressionMoyenne((int) progressionMoyenne);

                // Dernière activité
                Optional<Inscription> derniereInscription = inscriptionRepository.findByApprenantIdWithCours(etudiant.getId())
                        .stream().max(Comparator.comparing(Inscription::getDateDernierAcces));
                dto.setDerniereActivite(derniereInscription.map(Inscription::getDateDernierAcces).orElse(null));

                etudiantsDTO.add(dto);
            }

            model.addAttribute("professeur", professeur);
            model.addAttribute("etudiants", etudiantsDTO);
            model.addAttribute("coursList", coursList);
            model.addAttribute("pageTitle", "Suivi des étudiants");

        } catch (Exception e) {
            model.addAttribute("error", "Erreur lors du chargement des étudiants: " + e.getMessage());
        }

        return "professeur/etudiants/liste";
    }

    // Détail d'un étudiant avec sa progression par cours
    @GetMapping("/{etudiantId}")
    public String detailEtudiant(@PathVariable Long etudiantId,
                                 @AuthenticationPrincipal UserDetails userDetails,
                                 Model model) {
        try {
            Utilisateur professeurUser = utilisateurRepository.findByEmail(userDetails.getUsername()).orElseThrow();
            Professeur professeur = professeurRepository.findByUtilisateur(professeurUser).orElseThrow();

            Utilisateur etudiant = utilisateurRepository.findById(etudiantId)
                    .orElseThrow(() -> new RuntimeException("Étudiant non trouvé"));

            // Récupérer les cours du professeur
            List<Cours> coursProfesseur = coursRepository.findByProfesseur(professeur);
            List<Long> coursIdsProfesseur = coursProfesseur.stream().map(Cours::getId).collect(Collectors.toList());

            // Récupérer les inscriptions de l'étudiant uniquement pour les cours du professeur
            List<Inscription> inscriptions = inscriptionRepository.findByApprenantIdWithCours(etudiantId)
                    .stream()
                    .filter(i -> coursIdsProfesseur.contains(i.getCours().getId()))
                    .collect(Collectors.toList());

            // Construire les DTOs de progression par cours
            List<ProgressionCoursDTO> progressionCours = new ArrayList<>();
            for (Inscription inscription : inscriptions) {
                ProgressionCoursDTO dto = new ProgressionCoursDTO();
                dto.setCoursId(inscription.getCours().getId());
                dto.setCoursTitre(inscription.getCours().getTitre());
                dto.setCoursDescriptionCourte(inscription.getCours().getDescriptionCourte());
                dto.setImageCouverture(inscription.getCours().getImageCouverture());

                // Calculer la progression
                int progression = calculerProgressionEtudiantCours(etudiantId, inscription.getCours().getId());
                dto.setProgression(progression);

                // Nombre de contenus complétés / total
                long totalContenus = contenuItemRepository.countByCoursId(inscription.getCours().getId());
                long contenusCompletes = progressionRepository.countCompletedByApprenantAndCours(etudiantId, inscription.getCours().getId());
                dto.setContenusCompletes((int) contenusCompletes);
                dto.setContenusTotal((int) totalContenus);

                dto.setDateDernierAcces(inscription.getDateDernierAcces());
                dto.setEstTermine(inscription.getTermine());

                progressionCours.add(dto);
            }

            // Statistiques globales
            StatistiquesEtudiantDTO statistiques = new StatistiquesEtudiantDTO();
            statistiques.setTotalCours(inscriptions.size());

            int progressionMoyenne = progressionCours.stream()
                    .mapToInt(ProgressionCoursDTO::getProgression)
                    .sum() / (inscriptions.isEmpty() ? 1 : inscriptions.size());
            statistiques.setProgressionMoyenne(progressionMoyenne);

            // Calculer les heures totales étudiées
            int heuresTotal = calculerHeuresTotalEtudiees(etudiantId, coursIdsProfesseur);
            statistiques.setTotalHeures(heuresTotal);

            model.addAttribute("professeur", professeur);
            model.addAttribute("etudiant", etudiant);
            model.addAttribute("progressionCours", progressionCours);
            model.addAttribute("statistiques", statistiques);
            model.addAttribute("pageTitle", "Détail - " + etudiant.getNomComplet());

        } catch (Exception e) {
            model.addAttribute("error", "Erreur: " + e.getMessage());
        }

        return "professeur/etudiants/detail";
    }

    // Recherche d'étudiants - CORRIGÉ
    @GetMapping("/recherche")
    @ResponseBody
    public List<EtudiantSuiviDTO> rechercherEtudiants(@RequestParam String keyword,
                                                      @AuthenticationPrincipal UserDetails userDetails) {
        try {
            Utilisateur utilisateur = utilisateurRepository.findByEmail(userDetails.getUsername()).orElse(null);
            if (utilisateur == null) return new ArrayList<>();

            Professeur professeur = professeurRepository.findByUtilisateur(utilisateur).orElse(null);
            if (professeur == null) return new ArrayList<>();

            // Récupérer les cours du professeur
            List<Cours> coursList = coursRepository.findByProfesseur(professeur);
            List<Long> coursIds = coursList.stream().map(Cours::getId).collect(Collectors.toList());

            // ✅ CORRECTION ICI - Utiliser searchByKeyword au lieu de findByNomContainingOrEmailContaining
            List<Utilisateur> etudiants = utilisateurRepository.searchByKeyword(keyword);

            List<EtudiantSuiviDTO> resultats = new ArrayList<>();
            for (Utilisateur etudiant : etudiants) {
                // Vérifier si l'étudiant est inscrit à au moins un cours du professeur
                List<Long> coursEtudiant = inscriptionRepository.findCoursIdsByApprenantId(etudiant.getId());
                boolean hasCommonCours = coursEtudiant.stream().anyMatch(coursIds::contains);

                if (hasCommonCours) {
                    EtudiantSuiviDTO dto = new EtudiantSuiviDTO();
                    dto.setId(etudiant.getId());
                    dto.setNomComplet(etudiant.getNomComplet());
                    dto.setEmail(etudiant.getEmail());
                    dto.setNomUtilisateur(etudiant.getNomUtilisateur());

                    long nbCours = coursEtudiant.stream().filter(coursIds::contains).count();
                    dto.setNombreCours((int) nbCours);

                    double progressionMoyenne = calculerProgressionMoyenne(etudiant.getId(), coursIds);
                    dto.setProgressionMoyenne((int) progressionMoyenne);

                    resultats.add(dto);
                }
            }

            return resultats;

        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    // Progression détaillée par matière pour un étudiant
    @GetMapping("/{etudiantId}/progression-matieres")
    @ResponseBody
    public List<ProgressionMatiereDTO> getProgressionParMatiere(@PathVariable Long etudiantId,
                                                                @AuthenticationPrincipal UserDetails userDetails) {
        List<ProgressionMatiereDTO> resultats = new ArrayList<>();

        try {
            Utilisateur professeurUser = utilisateurRepository.findByEmail(userDetails.getUsername()).orElse(null);
            if (professeurUser == null) return resultats;

            Professeur professeur = professeurRepository.findByUtilisateur(professeurUser).orElse(null);
            if (professeur == null) return resultats;

            // Récupérer les cours du professeur
            List<Cours> coursList = coursRepository.findByProfesseur(professeur);

            for (Cours cours : coursList) {
                // Vérifier si l'étudiant est inscrit
                boolean estInscrit = inscriptionRepository.existsByApprenantIdAndCoursId(etudiantId, cours.getId());
                if (estInscrit) {
                    int progression = calculerProgressionEtudiantCours(etudiantId, cours.getId());
                    ProgressionMatiereDTO dto = new ProgressionMatiereDTO();
                    dto.setNom(cours.getTitre());
                    dto.setDescription(cours.getDescriptionCourte());
                    dto.setProgression(progression);
                    resultats.add(dto);
                }
            }

        } catch (Exception e) {
            // Log error
        }

        return resultats;
    }

    // Méthodes utilitaires privées
    private double calculerProgressionMoyenne(Long etudiantId, List<Long> coursIds) {
        if (coursIds.isEmpty()) return 0;

        int totalProgression = 0;
        int count = 0;

        for (Long coursId : coursIds) {
            if (inscriptionRepository.existsByApprenantIdAndCoursId(etudiantId, coursId)) {
                totalProgression += calculerProgressionEtudiantCours(etudiantId, coursId);
                count++;
            }
        }

        return count == 0 ? 0 : (double) totalProgression / count;
    }

    private int calculerProgressionEtudiantCours(Long etudiantId, Long coursId) {
        Long totalContenus = contenuItemRepository.countByCoursId(coursId);
        if (totalContenus == null || totalContenus == 0) return 0;

        Long contenusCompletes = progressionRepository.countCompletedByApprenantAndCours(etudiantId, coursId);
        if (contenusCompletes == null) return 0;

        return (int) (contenusCompletes * 100 / totalContenus);
    }

    private int calculerHeuresTotalEtudiees(Long etudiantId, List<Long> coursIds) {
        List<Progression> progressions = progressionRepository.findByApprenantIdOrderByDateDebutDesc(etudiantId);
        int totalSecondes = progressions.stream()
                .filter(p -> p.getContenuItem() != null &&
                        p.getContenuItem().getSection() != null &&
                        p.getContenuItem().getSection().getCours() != null &&
                        coursIds.contains(p.getContenuItem().getSection().getCours().getId()))
                .mapToInt(p -> p.getTempsPasse() != null ? p.getTempsPasse() : 0)
                .sum();
        return totalSecondes / 3600; // Convertir en heures
    }
}