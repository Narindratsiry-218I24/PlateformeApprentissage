package com.plateforme_etudiant.demo.controller.web;

import com.plateforme_etudiant.demo.dto.CourseResponseDTO;
import com.plateforme_etudiant.demo.dto.ProfesseurResponseDTO;
import com.plateforme_etudiant.demo.service.CourseServiceFacade;
import com.plateforme_etudiant.demo.service.ProfesseurService;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/professeur")
public class ProfesseurDashboardController {

    private static final Logger log = LoggerFactory.getLogger(ProfesseurDashboardController.class);

    @Autowired
    private ProfesseurService professeurService;

    @Autowired
    private CourseServiceFacade courseService;

    @GetMapping("/dashboard")
    public String dashboard(Model model, HttpSession session) {
        log.info("=== ACCÈS DASHBOARD ===");

        Long professeurId = (Long) session.getAttribute("professeurId");

        log.info("professeurId en session: {}", professeurId);

        if (professeurId == null) {
            log.warn("❌ Pas de professeurId en session, redirection vers login");
            return "redirect:/login";
        }

        try {
            ProfesseurResponseDTO professeur = professeurService.getProfesseurById(professeurId);
            List<CourseResponseDTO> cours = courseService.getCoursParProfesseur(professeurId);

            long totalEtudiants = cours.stream()
                    .mapToLong(c -> c.getNombreApprenants() != null ? c.getNombreApprenants() : 0)
                    .sum();
            long coursPublies = cours.stream().filter(CourseResponseDTO::getPublie).count();

            model.addAttribute("professeur", professeur);
            model.addAttribute("cours", cours);
            model.addAttribute("totalCours", cours.size());
            model.addAttribute("coursPublies", coursPublies);
            model.addAttribute("totalEtudiants", totalEtudiants);
            model.addAttribute("progressionMoyenne", 78);
            model.addAttribute("pageTitle", "Dashboard Professeur");

            log.info("✅ Dashboard affiché pour le professeur: {}", professeur.getEmail());
            return "professeur/dashboard";

        } catch (Exception e) {
            log.error("❌ Erreur dashboard: {}", e.getMessage(), e);
            model.addAttribute("error", "Erreur: " + e.getMessage());
            return "redirect:/login";
        }
    }
}