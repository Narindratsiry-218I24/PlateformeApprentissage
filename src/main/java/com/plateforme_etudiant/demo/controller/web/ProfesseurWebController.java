package com.plateforme_etudiant.demo.controller.web;

import com.plateforme_etudiant.demo.dto.ProfesseurResponseDTO;
import com.plateforme_etudiant.demo.service.ProfesseurService;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/professeur")
public class ProfesseurWebController {

    private static final Logger log = LoggerFactory.getLogger(ProfesseurWebController.class);

    @Autowired
    private ProfesseurService professeurService;

    private boolean checkSessionAndAddProfesseur(Model model, HttpSession session) {
        Long professeurId = (Long) session.getAttribute("professeurId");
        if (professeurId == null) {
            log.warn("❌ Pas de professeurId en session, redirection");
            return false;
        }
        try {
            ProfesseurResponseDTO professeur = professeurService.getProfesseurById(professeurId);
            model.addAttribute("professeur", professeur);
            return true;
        } catch (Exception e) {
            log.error("❌ Erreur de récupération du professeur: {}", e.getMessage());
            return false;
        }
    }

    @GetMapping("/alertes")
    public String alertes(Model model, HttpSession session) {
        if (!checkSessionAndAddProfesseur(model, session)) {
            return "redirect:/login";
        }
        model.addAttribute("currentPage", "alertes");
        model.addAttribute("pageTitle", "Alertes & Notifications - Professeur");
        return "professeur/alertes";
    }

    @GetMapping("/statistiques")
    public String statistiques(Model model, HttpSession session) {
        if (!checkSessionAndAddProfesseur(model, session)) {
            return "redirect:/login";
        }
        model.addAttribute("currentPage", "statistiques");
        model.addAttribute("pageTitle", "Statistiques - Professeur");
        return "professeur/statistiques";
    }

    @GetMapping("/profil")
    public String profil(Model model, HttpSession session) {
        if (!checkSessionAndAddProfesseur(model, session)) {
            return "redirect:/login";
        }
        model.addAttribute("currentPage", "profil");
        model.addAttribute("pageTitle", "Mon Profil - Professeur");
        return "professeur/profil";
    }

    @GetMapping("/parametres")
    public String parametres(Model model, HttpSession session) {
        if (!checkSessionAndAddProfesseur(model, session)) {
            return "redirect:/login";
        }
        model.addAttribute("currentPage", "parametres");
        model.addAttribute("pageTitle", "Paramètres - Professeur");
        return "professeur/parametres";
    }
}
