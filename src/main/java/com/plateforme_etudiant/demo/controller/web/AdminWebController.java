package com.plateforme_etudiant.demo.controller.web;

import com.plateforme_etudiant.demo.dto.ProfesseurRequestDTO;
import com.plateforme_etudiant.demo.dto.ProfesseurResponseDTO;
import com.plateforme_etudiant.demo.model.Professeur;
import com.plateforme_etudiant.demo.model.Utilisateur;
import com.plateforme_etudiant.demo.model.enums.Role;
import com.plateforme_etudiant.demo.service.ProfesseurService;
import com.plateforme_etudiant.demo.service.UtilisateurService;
import com.plateforme_etudiant.demo.repository.CoursRepository;
import com.plateforme_etudiant.demo.repository.InscriptionRepository;
import com.plateforme_etudiant.demo.repository.ProfesseurRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminWebController {

    private final UtilisateurService utilisateurService;
    private final ProfesseurService professeurService;
    private final CoursRepository coursRepository;
    private final InscriptionRepository inscriptionRepository;
    private final ProfesseurRepository professeurRepository;

    public AdminWebController(UtilisateurService utilisateurService,
                             ProfesseurService professeurService,
                             CoursRepository coursRepository,
                             InscriptionRepository inscriptionRepository,
                             ProfesseurRepository professeurRepository) {
        this.utilisateurService = utilisateurService;
        this.professeurService = professeurService;
        this.coursRepository = coursRepository;
        this.inscriptionRepository = inscriptionRepository;
        this.professeurRepository = professeurRepository;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, HttpSession session) {
        // Vérification rôle via session
        String role = (String) session.getAttribute("userRole");
        if (role == null || !role.equals(Role.ADMINISTRATEUR.name())) {
            return "redirect:/login?error=Accès+réservé+aux+administrateurs";
        }

        model.addAttribute("pageTitle", "Tableau de Bord Admin");
        model.addAttribute("countEtudiants", utilisateurService.countAllApprenants());
        model.addAttribute("countProfesseurs", utilisateurService.countAllProfesseurs());
        model.addAttribute("countCours", coursRepository.count());
        model.addAttribute("countInscriptions", inscriptionRepository.count());

        return "admin/dashboard";
    }

    // --- GESTION PROFESSEURS ---

    @GetMapping("/professeurs")
    public String listeProfesseurs(Model model) {
        model.addAttribute("pageTitle", "Gestion des Professeurs");
        model.addAttribute("professeurs", professeurService.getAllProfesseurs());
        return "admin/professeurs/liste";
    }

    @PostMapping("/professeurs/creer")
    public String creerProfesseur(@ModelAttribute ProfesseurRequestDTO request, RedirectAttributes redirectAttributes) {
        try {
            professeurService.creerProfesseur(request);
            redirectAttributes.addFlashAttribute("success", "Professeur créé avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur: " + e.getMessage());
        }
        return "redirect:/admin/professeurs";
    }

    @PostMapping("/professeurs/modifier/{id}")
    public String modifierProfesseur(@PathVariable Long id, @ModelAttribute ProfesseurRequestDTO request, RedirectAttributes redirectAttributes) {
        try {
            professeurService.updateProfesseur(id, request);
            redirectAttributes.addFlashAttribute("success", "Professeur modifié avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur: " + e.getMessage());
        }
        return "redirect:/admin/professeurs";
    }

    @GetMapping("/professeurs/supprimer/{id}")
    public String supprimerProfesseur(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            professeurService.deleteProfesseur(id);
            redirectAttributes.addFlashAttribute("success", "Professeur supprimé avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur: " + e.getMessage());
        }
        return "redirect:/admin/professeurs";
    }

    // --- GESTION ÉTUDIANTS ---

    @GetMapping("/etudiants")
    public String listeEtudiants(Model model) {
        model.addAttribute("pageTitle", "Gestion des Étudiants");
        model.addAttribute("etudiants", utilisateurService.findAllApprenants());
        return "admin/etudiants/liste";
    }

    @PostMapping("/etudiants/creer")
    public String creerEtudiant(@RequestParam String nomUtilisateur, 
                               @RequestParam String email,
                               @RequestParam String motDePasse,
                               @RequestParam String prenom,
                               @RequestParam String nom,
                               RedirectAttributes redirectAttributes) {
        try {
            utilisateurService.creerUtilisateur(nomUtilisateur, email, motDePasse, prenom, nom, Role.APPRENANT);
            redirectAttributes.addFlashAttribute("success", "Étudiant créé avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur: " + e.getMessage());
        }
        return "redirect:/admin/etudiants";
    }

    @PostMapping("/etudiants/modifier/{id}")
    public String modifierEtudiant(@PathVariable Long id,
                                  @RequestParam String prenom,
                                  @RequestParam String nom,
                                  @RequestParam String email,
                                  @RequestParam(required = false) Boolean actif,
                                  RedirectAttributes redirectAttributes) {
        try {
            utilisateurService.updateUtilisateur(id, prenom, nom, email, actif != null ? actif : false);
            redirectAttributes.addFlashAttribute("success", "Étudiant modifié avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur: " + e.getMessage());
        }
        return "redirect:/admin/etudiants";
    }

    @GetMapping("/etudiants/supprimer/{id}")
    public String supprimerEtudiant(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            utilisateurService.deleteUtilisateur(id);
            redirectAttributes.addFlashAttribute("success", "Étudiant supprimé avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur: " + e.getMessage());
        }
        return "redirect:/admin/etudiants";
    }

    // --- SUIVI ---

    @GetMapping("/suivi")
    public String suivi(Model model) {
        model.addAttribute("pageTitle", "Suivi Global");
        model.addAttribute("inscriptions", inscriptionRepository.findAll());
        return "admin/suivi/liste";
    }

    // ─────────────────────────────────────────────────────────────────────────
    // CRÉATION UTILISATEUR UNIVERSEL (admin / étudiant / professeur)
    // Accessible uniquement par l'admin — sécurité via session userRole
    // ─────────────────────────────────────────────────────────────────────────

    @PostMapping("/utilisateurs/creer")
    public String creerUtilisateur(@RequestParam(required = false) String nomUtilisateur,
                                   @RequestParam(required = false) String email,
                                   @RequestParam(required = false) String motDePasse,
                                   @RequestParam(required = false) String prenom,
                                   @RequestParam(required = false) String nom,
                                   @RequestParam(required = false) String role,
                                   @RequestParam(required = false) String specialite,
                                   @RequestParam(required = false) String bio,
                                   HttpSession session,
                                   RedirectAttributes redirectAttributes) {
        
        // Vérification admin
        String userRole = (String) session.getAttribute("userRole");
        if (userRole == null || !userRole.equals(Role.ADMINISTRATEUR.name())) {
            return "redirect:/login?error=Acc%C3%A8s+refus%C3%A9";
        }

        System.out.println("=== TENTATIVE CRÉATION UTILISATEUR (FORM) ===");
        System.out.println("nomUtilisateur: " + nomUtilisateur);
        System.out.println("role: " + role);

        if (nomUtilisateur == null || email == null || motDePasse == null || role == null) {
            redirectAttributes.addFlashAttribute("error", "Erreur : Paramètres obligatoires manquants (nomUtilisateur, email, motDePasse, role)");
            return "redirect:/admin/dashboard";
        }

        try {
            Role roleEnum = Role.valueOf(role.toUpperCase());
            Utilisateur u = utilisateurService.creerUtilisateur(nomUtilisateur, email, motDePasse, prenom, nom, roleEnum);

            if (roleEnum == Role.PROFESSEUR) {
                professeurRepository.findByUtilisateur(u).orElseGet(() -> {
                    Professeur p = new Professeur();
                    p.setUtilisateur(u);
                    p.setSpecialite(specialite != null ? specialite : "Non définie");
                    p.setBiographie(bio != null ? bio : "");
                    p.setVerifie(true);
                    p.setDateCreation(java.time.LocalDateTime.now());
                    return professeurRepository.save(p);
                });
            }
            redirectAttributes.addFlashAttribute("success", "Utilisateur " + nomUtilisateur + " créé avec succès !");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur : " + e.getMessage());
        }

        return "redirect:/admin/dashboard";
    }
}
