package com.plateforme_etudiant.demo.controller.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeWebController {

    @GetMapping("/")
    public String accueil() {
        return "acceuil";
    }

    @GetMapping("/cours")
    public String cours() {
        
        return "acceuil";
    }
}