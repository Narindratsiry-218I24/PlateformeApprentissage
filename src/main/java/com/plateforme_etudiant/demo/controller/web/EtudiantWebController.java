// controller/web/EtudiantWebController.java
package com.plateforme_etudiant.demo.controller.web;

import com.plateforme_etudiant.demo.dto.*;
import com.plateforme_etudiant.demo.service.EtudiantService;
import com.plateforme_etudiant.demo.service.EtudiantCoursDetailService;
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

    public EtudiantWebController(EtudiantService etudiantService,
                                 EtudiantCoursDetailService coursDetailService) {
        this.etudiantService = etudiantService;
        this.coursDetailService = coursDetailService;
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
            return "etudiant/dashboard";

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

            return "etudiant/mes-cours";

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

            model.addAttribute("etudiant", etudiant);
            model.addAttribute("cours", coursInscrits);
            model.addAttribute("progressionsParMatiere", progressionsParMatiere);

            return "etudiant/progression";

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

            return "etudiant/catalogue";

        } catch (Exception e) {
            log.error("❌ Erreur: {}", e.getMessage());
            return "etudiant/catalogue";
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

            model.addAttribute("cours", coursDetail);
            log.info("✅ Cours chargé avec {} sections", coursDetail.getSections() != null ? coursDetail.getSections().size() : 0);

            return "etudiant/visionner-cours";

        } catch (Exception e) {
            log.error("❌ Erreur visionnage cours: {}", e.getMessage(), e);
            return "redirect:/etudiant/mes-cours";
        }
    }

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
            etudiantService.completerContenu(utilisateurId, contenuId);
            int nouvelleProgression = etudiantService.calculerProgressionCours(utilisateurId, coursId);

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