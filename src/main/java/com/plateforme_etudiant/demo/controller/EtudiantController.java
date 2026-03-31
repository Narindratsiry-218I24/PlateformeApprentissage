package com.plateforme_etudiant.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.*;

@Controller
@RequestMapping("/professeur/etudiants")
public class EtudiantController {

    @GetMapping
    public String listeEtudiants(Model model) {
        // Données mockées pour les étudiants
        List<Map<String, Object>> etudiants = new ArrayList<>();

        Map<String, Object> etudiant1 = new HashMap<>();
        etudiant1.put("id", 1L);
        etudiant1.put("prenom", "Sophie");
        etudiant1.put("nom", "Martin");
        etudiant1.put("classe", "Terminale S");
        etudiant1.put("email", "sophie.martin@email.com");
        etudiant1.put("avatar", "https://randomuser.me/api/portraits/women/1.jpg");
        etudiant1.put("progression", 78);
        etudiant1.put("leconsVues", 45);
        etudiant1.put("leconsTotales", 58);
        etudiant1.put("derniereActivite", "2024-03-24T10:30:00");
        etudiant1.put("statut", "ACTIF");
        etudiants.add(etudiant1);

        Map<String, Object> etudiant2 = new HashMap<>();
        etudiant2.put("id", 2L);
        etudiant2.put("prenom", "Thomas");
        etudiant2.put("nom", "Bernard");
        etudiant2.put("classe", "Première");
        etudiant2.put("email", "thomas.bernard@email.com");
        etudiant2.put("avatar", "https://randomuser.me/api/portraits/men/2.jpg");
        etudiant2.put("progression", 45);
        etudiant2.put("leconsVues", 26);
        etudiant2.put("leconsTotales", 58);
        etudiant2.put("derniereActivite", "2024-03-23T14:15:00");
        etudiant2.put("statut", "ACTIF");
        etudiants.add(etudiant2);

        Map<String, Object> etudiant3 = new HashMap<>();
        etudiant3.put("id", 3L);
        etudiant3.put("prenom", "Emma");
        etudiant3.put("nom", "Dubois");
        etudiant3.put("classe", "Terminale ES");
        etudiant3.put("email", "emma.dubois@email.com");
        etudiant3.put("avatar", "https://randomuser.me/api/portraits/women/3.jpg");
        etudiant3.put("progression", 92);
        etudiant3.put("leconsVues", 53);
        etudiant3.put("leconsTotales", 58);
        etudiant3.put("derniereActivite", "2024-03-24T09:45:00");
        etudiant3.put("statut", "EXCELLENT");
        etudiants.add(etudiant3);

        Map<String, Object> etudiant4 = new HashMap<>();
        etudiant4.put("id", 4L);
        etudiant4.put("prenom", "Lucas");
        etudiant4.put("nom", "Petit");
        etudiant4.put("classe", "Seconde");
        etudiant4.put("email", "lucas.petit@email.com");
        etudiant4.put("avatar", "https://randomuser.me/api/portraits/men/4.jpg");
        etudiant4.put("progression", 23);
        etudiant4.put("leconsVues", 13);
        etudiant4.put("leconsTotales", 58);
        etudiant4.put("derniereActivite", "2024-03-20T11:20:00");
        etudiant4.put("statut", "INACTIF");
        etudiants.add(etudiant4);

        // Statistiques
        Map<String, Object> stats = new HashMap<>();
        stats.put("total", etudiants.size());
        stats.put("moyenne", 61);
        stats.put("actifs", 3);
        stats.put("enDifficulte", 1);

        // ========== IMPORTANT: Ajouter l'objet professeur ==========
        Map<String, Object> professeur = new HashMap<>();
        professeur.put("prenom", "Martin");
        professeur.put("nom", "Dupont");
        professeur.put("specialite", "Mathématiques");
        professeur.put("email", "martin.dupont@email.com");
        // ============================================================

        model.addAttribute("etudiants", etudiants);
        model.addAttribute("stats", stats);
        model.addAttribute("professeur", professeur);  // <-- AJOUT OBLIGATOIRE
        model.addAttribute("currentPage", "etudiants");
        model.addAttribute("pageTitle", "Mes étudiants - Professeur");

        return "professeur/etudiants/liste";
    }
}