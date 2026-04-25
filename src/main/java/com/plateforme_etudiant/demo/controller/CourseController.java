package com.plateforme_etudiant.demo.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.plateforme_etudiant.demo.dto.CourseRequestDTO;
import com.plateforme_etudiant.demo.dto.CourseResponseDTO;
import com.plateforme_etudiant.demo.model.Professeur;
import com.plateforme_etudiant.demo.repository.ProfesseurRepository;
import com.plateforme_etudiant.demo.service.CourseServiceFacade;
import com.plateforme_etudiant.demo.service.ProfesseurService;
import com.plateforme_etudiant.demo.service.FileUploadService;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

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

    @Autowired
    private ProfesseurService professeurService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @GetMapping
    public String listerCours(Model model, HttpSession session) {
        Professeur prof = getProfesseurFromSession(session);
        if (prof == null) return "redirect:/login";
        model.addAttribute("cours", courseService.getCoursParProfesseur(prof.getId()));
        model.addAttribute("professeur", professeurService.getProfesseurById(prof.getId()));
        return "professeur/cours/liste";
    }

    @GetMapping("/ajouter")
    public String ajouterCours(Model model, HttpSession session) {
        Professeur prof = getProfesseurFromSession(session);
        if (prof == null) return "redirect:/login";
        model.addAttribute("professeur", professeurService.getProfesseurById(prof.getId()));
        model.addAttribute("isEditMode", false);
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
            @RequestParam(value = "fichiers", required = false) List<MultipartFile> fichiers,
            HttpSession session) {

        Map<String, Object> response = new HashMap<>();

        try {
            Professeur professeur = getProfesseurFromSession(session);
            if (professeur == null) {
                response.put("success", false);
                response.put("message", "Session expiree. Veuillez vous reconnecter.");
                return ResponseEntity.status(401).body(response);
            }

            String imageUrl = null;
            if (imageCouverture != null && !imageCouverture.isEmpty()) {
                imageUrl = fileUploadService.uploadCoverImage(imageCouverture);
            }

            CourseRequestDTO request = buildCourseRequest(
                    titre, descriptionCourte, description, dureeEstimee, imageUrl, sectionsJson, publier, fichiers
            );

            CourseResponseDTO coursCree = courseService.creerCours(request, professeur.getId());

            response.put("success", true);
            response.put("message", publier ? "Cours publie avec succes !" : "Cours sauvegarde en brouillon");
            response.put("coursId", coursCree.getId());
            response.put("publie", coursCree.getPublie());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Erreur lors de la creation du cours", e);
            response.put("success", false);
            response.put("message", "Erreur serveur : " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @GetMapping("/{coursId}/gerer")
    public String gererCours(@PathVariable Long coursId, Model model, HttpSession session) {
        Professeur prof = getProfesseurFromSession(session);
        if (prof == null) return "redirect:/login";

        try {
            CourseResponseDTO cours = courseService.getCoursParIdPourProfesseur(coursId, prof.getId());
            model.addAttribute("cours", cours);
            model.addAttribute("professeur", professeurService.getProfesseurById(prof.getId()));
            return "professeur/cours/gerer";
        } catch (Exception e) {
            log.error("Erreur lors de la recuperation du cours: {}", e.getMessage());
            model.addAttribute("error", "Cours non trouve");
            return "redirect:/professeur/cours";
        }
    }

    @GetMapping("/{coursId}/modifier")
    public String modifierCours(@PathVariable Long coursId, Model model, HttpSession session) {
        Professeur prof = getProfesseurFromSession(session);
        if (prof == null) return "redirect:/login";

        try {
            CourseResponseDTO cours = courseService.getCoursParIdPourProfesseur(coursId, prof.getId());
            model.addAttribute("cours", cours);
            model.addAttribute("professeur", professeurService.getProfesseurById(prof.getId()));
            model.addAttribute("isEditMode", true);
            return "professeur/cours/modifier";
        } catch (Exception e) {
            log.error("Erreur lors de l'ouverture de la modification du cours: {}", e.getMessage());
            return "redirect:/professeur/cours";
        }
    }

    @PostMapping("/{coursId}/api/modifier-avec-fichier")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> modifierCoursAvecFichier(
            @PathVariable Long coursId,
            @RequestParam("titre") String titre,
            @RequestParam(value = "descriptionCourte", required = false) String descriptionCourte,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "dureeEstimee", required = false) Integer dureeEstimee,
            @RequestParam(value = "imageCouverture", required = false) MultipartFile imageCouverture,
            @RequestParam(value = "imageCouvertureExistante", required = false) String imageCouvertureExistante,
            @RequestParam(value = "sections", required = false) String sectionsJson,
            @RequestParam(value = "publier", required = false, defaultValue = "false") boolean publier,
            @RequestParam(value = "fichiers", required = false) List<MultipartFile> fichiers,
            HttpSession session) {

        Map<String, Object> response = new HashMap<>();

        try {
            Professeur professeur = getProfesseurFromSession(session);
            if (professeur == null) {
                response.put("success", false);
                response.put("message", "Session expiree. Veuillez vous reconnecter.");
                return ResponseEntity.status(401).body(response);
            }

            CourseResponseDTO coursExistant = courseService.getCoursParIdPourProfesseur(coursId, professeur.getId());
            String imageUrl = coursExistant.getImageCouverture();
            if (imageCouvertureExistante != null && !imageCouvertureExistante.isBlank()) {
                imageUrl = imageCouvertureExistante;
            }
            if (imageCouverture != null && !imageCouverture.isEmpty()) {
                imageUrl = fileUploadService.uploadCoverImage(imageCouverture);
            }

            CourseRequestDTO request = buildCourseRequest(
                    titre, descriptionCourte, description, dureeEstimee, imageUrl, sectionsJson, publier, fichiers
            );

            CourseResponseDTO coursModifie = courseService.mettreAJourCours(coursId, request);
            if (publier && !Boolean.TRUE.equals(coursModifie.getPublie())) {
                coursModifie = courseService.publierCours(coursId);
            }
            if (!publier && Boolean.TRUE.equals(coursModifie.getPublie())) {
                coursModifie = courseService.depublierCours(coursId);
            }

            response.put("success", true);
            response.put("message", "Cours mis a jour avec succes !");
            response.put("coursId", coursModifie.getId());
            response.put("publie", coursModifie.getPublie());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Erreur lors de la modification du cours", e);
            response.put("success", false);
            response.put("message", "Erreur serveur : " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @PostMapping("/{coursId}/supprimer")
    public String supprimerCours(@PathVariable Long coursId, HttpSession session) {
        Professeur prof = getProfesseurFromSession(session);
        if (prof == null) return "redirect:/login";

        try {
            courseService.supprimerCoursPourProfesseur(coursId, prof.getId());
            log.info("Cours {} supprime avec succes", coursId);
        } catch (Exception e) {
            log.error("Erreur lors de la suppression du cours: {}", e.getMessage());
        }

        return "redirect:/professeur/cours";
    }

    private CourseRequestDTO buildCourseRequest(String titre,
                                                String descriptionCourte,
                                                String description,
                                                Integer dureeEstimee,
                                                String imageUrl,
                                                String sectionsJson,
                                                boolean publier,
                                                List<MultipartFile> fichiers) throws Exception {
        CourseRequestDTO request = new CourseRequestDTO();
        request.setTitre(titre);
        request.setDescriptionCourte(descriptionCourte);
        request.setDescription(description);
        request.setDureeEstimee(dureeEstimee != null ? dureeEstimee : 0);
        request.setImageCouverture(imageUrl);
        request.setPublie(publier);

        if (sectionsJson != null && !sectionsJson.trim().isEmpty()) {
            List<CourseRequestDTO.SectionDTO> sectionsList = objectMapper.readValue(
                    sectionsJson,
                    new TypeReference<List<CourseRequestDTO.SectionDTO>>() {}
            );
            request.setSections(sectionsList);

            if (fichiers != null && !fichiers.isEmpty()) {
                request = processUploadedFiles(request, fichiers);
            }
        }

        return request;
    }

    private CourseRequestDTO processUploadedFiles(CourseRequestDTO request, List<MultipartFile> fichiers) {
        int fileIndex = 0;

        if (request.getSections() == null) {
            return request;
        }

        for (CourseRequestDTO.SectionDTO section : request.getSections()) {
            if (section.getChapitres() == null) {
                continue;
            }

            for (CourseRequestDTO.ChapitreDTO chapitre : section.getChapitres()) {
                if (chapitre.getContenus() == null) {
                    continue;
                }

                for (CourseRequestDTO.ContenuDTO contenu : chapitre.getContenus()) {
                    String type = contenu.getTypeContenu();
                    if (!(("PDF".equals(type) || "IMAGE".equals(type)) &&
                            contenu.getFichierUrl() != null &&
                            !contenu.getFichierUrl().isEmpty() &&
                            fileIndex < fichiers.size())) {
                        continue;
                    }

                    MultipartFile file = fichiers.get(fileIndex);
                    try {
                        String uploadedUrl = fileUploadService.uploadContentFile(file);
                        contenu.setFichierUrl(uploadedUrl);
                    } catch (Exception e) {
                        log.error("Erreur upload fichier: {}", e.getMessage());
                    }
                    fileIndex++;
                }
            }
        }

        return request;
    }

    private Professeur getProfesseurFromSession(HttpSession session) {
        Long professeurId = (Long) session.getAttribute("professeurId");
        if (professeurId == null) return null;
        return professeurRepository.findById(professeurId).orElse(null);
    }
}
