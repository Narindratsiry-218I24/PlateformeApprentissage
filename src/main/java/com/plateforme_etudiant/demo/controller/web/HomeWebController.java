package com.plateforme_etudiant.demo.controller.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class HomeWebController {

    private final com.plateforme_etudiant.demo.service.cours.CourseReadService courseReadService;

    public HomeWebController(com.plateforme_etudiant.demo.service.cours.CourseReadService courseReadService) {
        this.courseReadService = courseReadService;
    }

    @GetMapping("/")
    public String accueil(org.springframework.ui.Model model) {
        model.addAttribute("derniersCours", courseReadService.getCoursPublies().stream().limit(4).collect(java.util.stream.Collectors.toList()));
        return "Acceuil";
    }

    @GetMapping("/cours")
    public String cours(org.springframework.ui.Model model) {
        model.addAttribute("derniersCours", courseReadService.getCoursPublies());
        return "Acceuil";
    }

    @PostMapping("/subscribe")
    public String subscribe(@RequestParam String email, RedirectAttributes redirectAttributes) {
        if (email == null || email.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Veuillez saisir une adresse email.");
            return "redirect:/";
        }
        redirectAttributes.addFlashAttribute("success", "Merci ! Vous êtes bien abonné avec " + email.trim() + ".");
        return "redirect:/";
    }
}