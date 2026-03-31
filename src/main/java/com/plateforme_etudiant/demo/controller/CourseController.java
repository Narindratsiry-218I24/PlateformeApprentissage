// CourseController.java
package com.plateforme_etudiant.demo.controller;

import com.plateforme_etudiant.demo.dto.CourseRequestDTO;
import com.plateforme_etudiant.demo.dto.CourseResponseDTO;
import com.plateforme_etudiant.demo.service.CourseServiceFacade;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/professeur/cours")
public class CourseController {

    private final CourseServiceFacade courseService;

    // Constructeur explicite
    public CourseController(CourseServiceFacade courseService) {
        this.courseService = courseService;
    }

    // ==================== CRÉATION ====================

    @PostMapping
    public ResponseEntity<CourseResponseDTO> creerCours(
            @Valid @RequestBody CourseRequestDTO request,
            @RequestParam Long professeurId) {
        CourseResponseDTO coursCree = courseService.creerCours(request, professeurId);
        return new ResponseEntity<>(coursCree, HttpStatus.CREATED);
    }

    // ==================== LECTURE ====================

    @GetMapping
    public ResponseEntity<List<CourseResponseDTO>> getCoursParProfesseur(
            @RequestParam Long professeurId) {
        List<CourseResponseDTO> cours = courseService.getCoursParProfesseur(professeurId);
        return ResponseEntity.ok(cours);
    }

    @GetMapping("/publies")
    public ResponseEntity<List<CourseResponseDTO>> getCoursPublies() {
        List<CourseResponseDTO> cours = courseService.getCoursPublies();
        return ResponseEntity.ok(cours);
    }

    @GetMapping("/{coursId}")
    public ResponseEntity<CourseResponseDTO> getCoursParId(@PathVariable Long coursId) {
        CourseResponseDTO cours = courseService.getCoursParId(coursId);
        return ResponseEntity.ok(cours);
    }

    @GetMapping("/search")
    public ResponseEntity<List<CourseResponseDTO>> searchCours(@RequestParam String keyword) {
        List<CourseResponseDTO> cours = courseService.searchCours(keyword);
        return ResponseEntity.ok(cours);
    }

    // ==================== MISE À JOUR ====================

    @PutMapping("/{coursId}")
    public ResponseEntity<CourseResponseDTO> mettreAJourCours(
            @PathVariable Long coursId,
            @Valid @RequestBody CourseRequestDTO request) {
        CourseResponseDTO coursMisAJour = courseService.mettreAJourCours(coursId, request);
        return ResponseEntity.ok(coursMisAJour);
    }

    @PatchMapping("/{coursId}/publier")
    public ResponseEntity<CourseResponseDTO> publierCours(@PathVariable Long coursId) {
        CourseResponseDTO coursPublie = courseService.publierCours(coursId);
        return ResponseEntity.ok(coursPublie);
    }

    @PatchMapping("/{coursId}/depublier")
    public ResponseEntity<CourseResponseDTO> depublierCours(@PathVariable Long coursId) {
        CourseResponseDTO coursDepublie = courseService.depublierCours(coursId);
        return ResponseEntity.ok(coursDepublie);
    }

    // ==================== SUPPRESSION ====================

    @DeleteMapping("/{coursId}")
    public ResponseEntity<Void> supprimerCours(@PathVariable Long coursId) {
        courseService.supprimerCours(coursId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/sections/{sectionId}")
    public ResponseEntity<Void> supprimerSection(@PathVariable Long sectionId) {
        courseService.supprimerSection(sectionId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/chapitres/{chapitreId}")
    public ResponseEntity<Void> supprimerChapitre(@PathVariable Long chapitreId) {
        courseService.supprimerChapitre(chapitreId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/contenus/{contenuId}")
    public ResponseEntity<Void> supprimerContenu(@PathVariable Long contenuId) {
        courseService.supprimerContenuItem(contenuId);
        return ResponseEntity.noContent().build();
    }
}