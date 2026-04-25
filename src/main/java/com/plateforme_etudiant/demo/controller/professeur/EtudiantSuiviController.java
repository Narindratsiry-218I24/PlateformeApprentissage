package com.plateforme_etudiant.demo.controller.professeur;

import com.plateforme_etudiant.demo.dto.*;
import com.plateforme_etudiant.demo.model.*;
import com.plateforme_etudiant.demo.repository.*;
import com.plateforme_etudiant.demo.service.ProfesseurService;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/professeur/etudiants")
public class EtudiantSuiviController {

    private static final Logger log = LoggerFactory.getLogger(EtudiantSuiviController.class);

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

    @Autowired
    private ProfesseurService professeurService;

    // Méthode utilitaire pour récupérer le professeur depuis la session
    private Professeur getProfesseurFromSession(HttpSession session) {
        Long professeurId = (Long) session.getAttribute("professeurId");
        if (professeurId == null) {
            log.warn("❌ Aucun professeurId en session");
            return null;
        }
        return professeurRepository.findById(professeurId).orElse(null);
    }

    // Liste des étudiants du professeur
    @GetMapping
    public String listeEtudiants(Model model, HttpSession session) {
        log.info("=== LISTE ÉTUDIANTS ===");

        try {
            Professeur professeur = getProfesseurFromSession(session);
            if (professeur == null) {
                log.warn("❌ Professeur non authentifié, redirection login");
                return "redirect:/login";
            }

            // Récupérer tous les cours du professeur
            List<Cours> coursList = coursRepository.findByProfesseur(professeur);
            List<Long> coursIds = coursList.stream().map(Cours::getId).collect(Collectors.toList());

            if (coursIds.isEmpty()) {
                model.addAttribute("professeur", professeurService.getProfesseurById(professeur.getId()));
                model.addAttribute("etudiants", new ArrayList<>());
                model.addAttribute("coursList", coursList);
                model.addAttribute("pageTitle", "Suivi des étudiants");
                model.addAttribute("info", "Vous n'avez pas encore créé de cours.");
                return "professeur/etudiants/liste";
            }

            // Récupérer tous les étudiants inscrits à ces cours (sans doublons)
            Set<Utilisateur> etudiantsSet = new HashSet<>();
            for (Cours cours : coursList) {
                List<Inscription> inscriptions = inscriptionRepository.findByCoursIdWithApprenant(cours.getId());
                for (Inscription inscription : inscriptions) {
                    if (inscription.getApprenant() != null) {
                        etudiantsSet.add(inscription.getApprenant());
                    }
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

                // Calculer le nombre de cours suivis pour ce professeur
                long nbCours = 0;
                for (Long coursId : coursIds) {
                    if (inscriptionRepository.existsByApprenantIdAndCoursId(etudiant.getId(), coursId)) {
                        nbCours++;
                    }
                }
                dto.setNombreCours((int) nbCours);

                // Calculer la progression moyenne sur tous les cours
                double progressionMoyenne = calculerProgressionMoyenne(etudiant.getId(), coursIds);
                dto.setProgressionMoyenne((int) progressionMoyenne);

                // Dernière activité
                Optional<Inscription> derniereInscription = inscriptionRepository.findByApprenantIdWithCours(etudiant.getId())
                        .stream()
                        .filter(i -> coursIds.contains(i.getCours().getId()))
                        .max(Comparator.comparing(Inscription::getDateDernierAcces));
                dto.setDerniereActivite(derniereInscription.map(Inscription::getDateDernierAcces).orElse(null));

                etudiantsDTO.add(dto);
            }

            // Trier par nom
            etudiantsDTO.sort(Comparator.comparing(EtudiantSuiviDTO::getNomComplet));

            model.addAttribute("professeur", professeurService.getProfesseurById(professeur.getId()));
            model.addAttribute("etudiants", etudiantsDTO);
            model.addAttribute("coursList", coursList);
            model.addAttribute("pageTitle", "Suivi des étudiants");
            log.info("✅ {} étudiants trouvés", etudiantsDTO.size());

        } catch (Exception e) {
            log.error("❌ Erreur lors du chargement des étudiants: {}", e.getMessage(), e);
            model.addAttribute("error", "Erreur lors du chargement des étudiants: " + e.getMessage());
        }

        return "professeur/etudiants/liste";
    }

    // Détail d'un étudiant avec sa progression par cours
    @GetMapping("/{etudiantId}")
    public String detailEtudiant(@PathVariable Long etudiantId, Model model, HttpSession session) {
        log.info("=== DÉTAIL ÉTUDIANT {} ===", etudiantId);

        try {
            Professeur professeur = getProfesseurFromSession(session);
            if (professeur == null) {
                return "redirect:/login";
            }

            Utilisateur etudiant = utilisateurRepository.findById(etudiantId)
                    .orElseThrow(() -> new RuntimeException("Étudiant non trouvé"));

            // Récupérer les cours du professeur
            List<Cours> coursProfesseur = coursRepository.findByProfesseur(professeur);
            List<Long> coursIdsProfesseur = coursProfesseur.stream().map(Cours::getId).collect(Collectors.toList());

            // Récupérer les inscriptions de l'étudiant uniquement pour les cours du professeur
            List<Inscription> inscriptions = inscriptionRepository.findByApprenantIdAndProfesseurId(etudiantId, professeur.getId());

            if (inscriptions.isEmpty()) {
                log.warn("Acces refuse au detail etudiant {} pour le professeur {}", etudiantId, professeur.getId());
                model.addAttribute("professeur", professeurService.getProfesseurById(professeur.getId()));
                model.addAttribute("error", "Cet etudiant n'est inscrit a aucun de vos cours.");
                model.addAttribute("progressionCours", new ArrayList<>());
                model.addAttribute("statistiques", new StatistiquesEtudiantDTO());
                model.addAttribute("pageTitle", "Detail etudiant");
                return "professeur/etudiants/detail";
            }

        } catch (Exception e) {
            log.error("❌ Erreur: {}", e.getMessage(), e);
            model.addAttribute("error", "Erreur: " + e.getMessage());
        }

        return "professeur/etudiants/detail";
    }

    // Recherche d'étudiants
    @GetMapping("/recherche")
    @ResponseBody
    public List<EtudiantSuiviDTO> rechercherEtudiants(@RequestParam String keyword, HttpSession session) {
        log.info("=== RECHERCHE ÉTUDIANTS: {} ===", keyword);

        try {
            Professeur professeur = getProfesseurFromSession(session);
            if (professeur == null) {
                return new ArrayList<>();
            }

            // Récupérer les cours du professeur
            List<Cours> coursList = coursRepository.findByProfesseur(professeur);
            List<Long> coursIds = coursList.stream().map(Cours::getId).collect(Collectors.toList());

            if (coursIds.isEmpty()) {
                return new ArrayList<>();
            }

            // Rechercher les étudiants par mot-clé
            List<Utilisateur> etudiants = utilisateurRepository.searchByKeyword(keyword);

            List<EtudiantSuiviDTO> resultats = new ArrayList<>();
            for (Utilisateur etudiant : etudiants) {
                // Vérifier si l'étudiant est inscrit à au moins un cours du professeur
                boolean hasCommonCours = false;
                for (Long coursId : coursIds) {
                    if (inscriptionRepository.existsByApprenantIdAndCoursId(etudiant.getId(), coursId)) {
                        hasCommonCours = true;
                        break;
                    }
                }

                if (hasCommonCours) {
                    EtudiantSuiviDTO dto = new EtudiantSuiviDTO();
                    dto.setId(etudiant.getId());
                    dto.setNomComplet(etudiant.getNomComplet());
                    dto.setEmail(etudiant.getEmail());
                    dto.setNomUtilisateur(etudiant.getNomUtilisateur());

                    long nbCours = 0;
                    for (Long coursId : coursIds) {
                        if (inscriptionRepository.existsByApprenantIdAndCoursId(etudiant.getId(), coursId)) {
                            nbCours++;
                        }
                    }
                    dto.setNombreCours((int) nbCours);

                    double progressionMoyenne = calculerProgressionMoyenne(etudiant.getId(), coursIds);
                    dto.setProgressionMoyenne((int) progressionMoyenne);

                    resultats.add(dto);
                }
            }

            return resultats;

        } catch (Exception e) {
            log.error("❌ Erreur recherche: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    // Méthodes utilitaires privées
    private double calculerProgressionMoyenne(Long etudiantId, List<Long> coursIds) {
        if (coursIds == null || coursIds.isEmpty()) return 0;

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
                .filter(p -> p.getTempsPasse() != null)
                .mapToInt(Progression::getTempsPasse)
                .sum();
        return totalSecondes / 3600;
    }
}
