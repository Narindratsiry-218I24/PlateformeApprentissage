// controller/web/EtudiantWebController.java
package com.plateforme_etudiant.demo.controller.web;

import com.plateforme_etudiant.demo.dto.*;
import com.plateforme_etudiant.demo.service.EtudiantService;
import com.plateforme_etudiant.demo.service.EtudiantCoursDetailService;
import com.plateforme_etudiant.demo.service.ProgressionService;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/etudiant")
public class EtudiantWebController {

    private static final Logger log = LoggerFactory.getLogger(EtudiantWebController.class);

    private final EtudiantService etudiantService;
    private final EtudiantCoursDetailService coursDetailService;
    private final ProgressionService progressionService;

    public EtudiantWebController(EtudiantService etudiantService,
                                 EtudiantCoursDetailService coursDetailService,
                                 ProgressionService progressionService) {
        this.etudiantService = etudiantService;
        this.coursDetailService = coursDetailService;
        this.progressionService = progressionService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, HttpSession session) {
        log.info("=== ACCÈS DASHBOARD ÉTUDIANT ===");

        Long utilisateurId = (Long) session.getAttribute("userId");

        if (utilisateurId == null) {
            log.warn("❌ Pas d'utilisateur en session, redirection vers login");
            return "redirect:/login";
        }

        try {
            EtudiantResponseDTO etudiant = etudiantService.getEtudiantById(utilisateurId);
            List<CoursEtudiantDTO> coursEnCours = etudiantService.getCoursInscrits(utilisateurId);
            List<CoursEtudiantDTO> recommandations = etudiantService.getCoursRecommandes(utilisateurId);
            StatistiquesEtudiantDTO statistiques = etudiantService.getStatistiques(utilisateurId);

            model.addAttribute("etudiant", etudiant);
            model.addAttribute("coursEnCours", coursEnCours);
            model.addAttribute("recommandations", recommandations);
            model.addAttribute("statistiques", statistiques);

            log.info("✅ Dashboard étudiant affiché pour: {}", etudiant.getEmail());
            return "Etudiant/dashboard";

        } catch (Exception e) {
            log.error("❌ Erreur dashboard étudiant: {}", e.getMessage(), e);
            model.addAttribute("error", "Erreur: " + e.getMessage());
            return "redirect:/login";
        }
    }

    @GetMapping("/mes-cours")
    public String mesCours(Model model, HttpSession session) {
        log.info("=== ACCÈS MES COURS ===");

        Long utilisateurId = (Long) session.getAttribute("userId");

        if (utilisateurId == null) {
            return "redirect:/login";
        }

        try {
            EtudiantResponseDTO etudiant = etudiantService.getEtudiantById(utilisateurId);
            List<CoursEtudiantDTO> coursInscrits = etudiantService.getCoursInscrits(utilisateurId);

            model.addAttribute("etudiant", etudiant);
            model.addAttribute("cours", coursInscrits);

            return "Etudiant/mes-cours";

        } catch (Exception e) {
            log.error("❌ Erreur: {}", e.getMessage());
            model.addAttribute("error", "Erreur: " + e.getMessage());
            return "redirect:/etudiant/dashboard";
        }
    }

    @GetMapping("/progression")
    public String progression(Model model, HttpSession session) {
        log.info("=== ACCÈS PROGRESSION ===");

        Long utilisateurId = (Long) session.getAttribute("userId");

        if (utilisateurId == null) {
            return "redirect:/login";
        }

        try {
            EtudiantResponseDTO etudiant = etudiantService.getEtudiantById(utilisateurId);
            List<CoursEtudiantDTO> coursInscrits = etudiantService.getCoursInscrits(utilisateurId);
            List<ProgressionMatiereDTO> progressionsParMatiere = etudiantService.getProgressionsParMatiere(utilisateurId);
            List<ProgressionCoursDTO> progressionCours = progressionService.getProgressionParCours(utilisateurId);

            List<String> chartLabels = progressionCours.stream()
                    .map(ProgressionCoursDTO::getCoursTitre)
                    .toList();
            List<Integer> chartValues = progressionCours.stream()
                    .map(ProgressionCoursDTO::getProgression)
                    .toList();

            model.addAttribute("etudiant", etudiant);
            model.addAttribute("cours", coursInscrits);
            model.addAttribute("progressionsParMatiere", progressionsParMatiere);
            model.addAttribute("progressionCours", progressionCours);
            model.addAttribute("chartLabels", chartLabels);
            model.addAttribute("chartValues", chartValues);

            return "Etudiant/progression";

        } catch (Exception e) {
            log.error("❌ Erreur: {}", e.getMessage());
            model.addAttribute("error", "Erreur: " + e.getMessage());
            return "redirect:/etudiant/dashboard";
        }
    }

    @GetMapping("/catalogue")
    public String catalogue(Model model, HttpSession session) {
        log.info("=== ACCÈS CATALOGUE COURS ===");

        Long utilisateurId = (Long) session.getAttribute("userId");

        try {
            List<CoursEtudiantDTO> coursDisponibles = etudiantService.getCoursRecommandes(utilisateurId);
            model.addAttribute("cours", coursDisponibles);

            return "Etudiant/catalogue";

        } catch (Exception e) {
            log.error("❌ Erreur: {}", e.getMessage());
            return "Etudiant/catalogue";
        }
    }

    @PostMapping("/cours/{coursId}/inscrire")
    public String inscrireCours(@PathVariable Long coursId, HttpSession session) {
        log.info("=== INSCRIPTION AU COURS {} ===", coursId);

        Long utilisateurId = (Long) session.getAttribute("userId");

        if (utilisateurId == null) {
            return "redirect:/login";
        }

        try {
            etudiantService.inscrireCours(utilisateurId, coursId);
            log.info("✅ Inscription réussie au cours {}", coursId);

        } catch (Exception e) {
            log.error("❌ Erreur inscription: {}", e.getMessage());
        }

        return "redirect:/etudiant/mes-cours";
    }

    @GetMapping("/cours/{coursId}/continuer")
    public String continuerCours(@PathVariable Long coursId, HttpSession session) {
        log.info("=== CONTINUER COURS {} ===", coursId);

        Long utilisateurId = (Long) session.getAttribute("userId");

        if (utilisateurId == null) {
            return "redirect:/login";
        }

        return "redirect:/etudiant/cours/" + coursId + "/visionner";
    }

    // ⚠️ CORRECTION ICI - Utiliser CoursDetailDTO au lieu de CoursEtudiantDTO
    @GetMapping("/cours/{coursId}/visionner")
    public String visionnerCours(@PathVariable Long coursId, Model model, HttpSession session) {
        log.info("=== VISIONNAGE COURS {} ===", coursId);

        Long utilisateurId = (Long) session.getAttribute("userId");

        if (utilisateurId == null) {
            return "redirect:/login";
        }

        try {
            // Vérifier que l'étudiant est inscrit
            List<CoursEtudiantDTO> coursInscrits = etudiantService.getCoursInscrits(utilisateurId);
            boolean estInscrit = coursInscrits.stream().anyMatch(c -> c.getId().equals(coursId));

            if (!estInscrit) {
                log.warn("Étudiant {} non inscrit au cours {}", utilisateurId, coursId);
                return "redirect:/etudiant/mes-cours";
            }

            // Utiliser CoursDetailDTO pour avoir la structure complète
            CoursDetailDTO coursDetail = coursDetailService.getCoursDetail(coursId, utilisateurId);

            if (coursDetail == null) {
                log.error("Cours {} non trouvé", coursId);
                return "redirect:/etudiant/mes-cours";
            }

            EtudiantResponseDTO etudiant = etudiantService.getEtudiantById(utilisateurId);
            model.addAttribute("cours", coursDetail);
            model.addAttribute("etudiant", etudiant);
            log.info("✅ Cours chargé avec {} sections", coursDetail.getSections() != null ? coursDetail.getSections().size() : 0);

            return "Etudiant/visionner-cours";

        } catch (Exception e) {
            log.error("❌ Erreur visionnage cours: {}", e.getMessage(), e);
            return "redirect:/etudiant/mes-cours";
        }
    }



    // Ajouter dans EtudiantWebController.java

    @GetMapping("/cours/{coursId}/progression")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getProgressionCours(
            @PathVariable Long coursId,
            HttpSession session) {

        Long utilisateurId = (Long) session.getAttribute("userId");
        Map<String, Object> response = new HashMap<>();

        if (utilisateurId == null) {
            response.put("success", false);
            response.put("message", "Non authentifié");
            return ResponseEntity.status(401).body(response);
        }

        try {
            progressionService.verifierInscription(utilisateurId, coursId);
            int progression = progressionService.calculerProgressionCours(utilisateurId, coursId);
            response.put("success", true);
            response.put("progression", progression);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    //
    //
    //
    // Ajouter l'endpoint pour marquer un contenu comme complété
    @PostMapping("/cours/{coursId}/contenu/{contenuId}/completer")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> completerContenu(
            @PathVariable Long coursId,
            @PathVariable Long contenuId,
            HttpSession session) {

        Long utilisateurId = (Long) session.getAttribute("userId");
        Map<String, Object> response = new HashMap<>();

        if (utilisateurId == null) {
            response.put("success", false);
            response.put("message", "Non authentifié");
            return ResponseEntity.status(401).body(response);
        }

        try {
            int nouvelleProgression = progressionService.completerContenu(utilisateurId, coursId, contenuId);

            response.put("success", true);
            response.put("progression", nouvelleProgression);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Erreur completion contenu: {}", e.getMessage());
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}
