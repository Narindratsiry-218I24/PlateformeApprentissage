package com.plateforme_etudiant.demo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.plateforme_etudiant.demo.dto.CourseRequestDTO;
import com.plateforme_etudiant.demo.dto.CourseResponseDTO;
import com.plateforme_etudiant.demo.model.Professeur;
import com.plateforme_etudiant.demo.repository.ProfesseurRepository;
import com.plateforme_etudiant.demo.service.CourseServiceFacade;
import com.plateforme_etudiant.demo.service.FileUploadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/professeur/cours")
public class CourseController {

    private static final Logger log = LoggerFactory.getLogger(CourseController.class);

    @Autowired
    private CourseServiceFacade courseService;

    @Autowired
    private FileUploadService fileUploadService;

    @Autowired
    private ProfesseurRepository professeurRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @GetMapping
    public String listerCours(Model model, HttpSession session) {
        Professeur prof = getProfesseurFromSession(session);
        if (prof == null) return "redirect:/login";

        model.addAttribute("cours", courseService.getCoursParProfesseur(prof.getId()));
        model.addAttribute("professeur", prof);
        return "professeur/cours/liste";
    }

    @GetMapping("/ajouter")
    public String ajouterCours(Model model, HttpSession session) {
        Professeur prof = getProfesseurFromSession(session);
        if (prof == null) return "redirect:/login";
        model.addAttribute("professeur", prof);
        return "professeur/cours/ajouter";
    }

    @PostMapping("/api/creer-avec-fichier")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> creerCoursAvecFichier(
            @RequestParam("titre") String titre,
            @RequestParam(value = "descriptionCourte", required = false) String descriptionCourte,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "dureeEstimee", required = false) Integer dureeEstimee,
            @RequestParam(value = "imageCouverture", required = false) MultipartFile imageCouverture,
            @RequestParam(value = "sections", required = false) String sectionsJson,
            @RequestParam(value = "publier", required = false, defaultValue = "false") boolean publier,
            HttpSession session) {

        Map<String, Object> response = new HashMap<>();

        try {
            Professeur professeur = getProfesseurFromSession(session);
            if (professeur == null) {
                response.put("success", false);
                response.put("message", "Session expirée. Veuillez vous reconnecter.");
                return ResponseEntity.status(401).body(response);
            }

            log.info("📝 Création de cours - Titre: {}, Publier: {}", titre, publier);

            String imageUrl = null;
            if (imageCouverture != null && !imageCouverture.isEmpty()) {
                imageUrl = fileUploadService.uploadCoverImage(imageCouverture);
            }

            CourseRequestDTO request = new CourseRequestDTO();
            request.setTitre(titre);
            request.setDescriptionCourte(descriptionCourte);
            request.setDescription(description);
            request.setDureeEstimee(dureeEstimee != null ? dureeEstimee : 0);
            request.setImageCouverture(imageUrl);
            request.setPublie(publier);

            if (sectionsJson != null && !sectionsJson.trim().isEmpty()) {
                try {
                    List<CourseRequestDTO.SectionDTO> sectionsList = objectMapper.readValue(
                            sectionsJson,
                            objectMapper.getTypeFactory().constructCollectionType(List.class, CourseRequestDTO.SectionDTO.class)
                    );
                    request.setSections(sectionsList);
                    log.info("✅ {} sections reçues", sectionsList.size());

                    for (CourseRequestDTO.SectionDTO section : sectionsList) {
                        log.info("   Section: titre='{}'", section.getTitre());
                    }
                } catch (Exception e) {
                    log.error("❌ Erreur parsing JSON", e);
                    response.put("success", false);
                    response.put("message", "Erreur de format des sections: " + e.getMessage());
                    return ResponseEntity.badRequest().body(response);
                }
            }

            CourseResponseDTO coursCree = courseService.creerCours(request, professeur.getId());

            String message = publier ? "Cours publié avec succès !" : "Cours sauvegardé en brouillon";
            response.put("success", true);
            response.put("message", message);
            response.put("coursId", coursCree.getId());
            response.put("publie", coursCree.getPublie());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Erreur lors de la création du cours", e);
            response.put("success", false);
            response.put("message", "Erreur serveur : " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    // Ajouter dans CourseController.java

    @GetMapping("/{coursId}/gerer")
    public String gererCours(@PathVariable Long coursId, Model model, HttpSession session) {
        Professeur prof = getProfesseurFromSession(session);
        if (prof == null) return "redirect:/login";

        try {
            CourseResponseDTO cours = courseService.getCoursParId(coursId);
            model.addAttribute("cours", cours);
            model.addAttribute("professeur", prof);
            return "professeur/cours/gerer";
        } catch (Exception e) {
            log.error("Erreur lors de la récupération du cours: {}", e.getMessage());
            model.addAttribute("error", "Cours non trouvé");
            return "redirect:/professeur/cours";
        }
    }

    @PostMapping("/{coursId}/supprimer")
    public String supprimerCours(@PathVariable Long coursId, HttpSession session) {
        Professeur prof = getProfesseurFromSession(session);
        if (prof == null) return "redirect:/login";

        try {
            courseService.supprimerCours(coursId);
            log.info("Cours {} supprimé avec succès", coursId);
        } catch (Exception e) {
            log.error("Erreur lors de la suppression du cours: {}", e.getMessage());
        }

        return "redirect:/professeur/cours";
    }

    private Professeur getProfesseurFromSession(HttpSession session) {
        Long professeurId = (Long) session.getAttribute("professeurId");
        if (professeurId == null) return null;
        return professeurRepository.findById(professeurId).orElse(null);
    }
}