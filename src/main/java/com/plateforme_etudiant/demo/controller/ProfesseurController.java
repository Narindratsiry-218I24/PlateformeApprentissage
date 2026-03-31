// ProfesseurController.java
package com.plateforme_etudiant.demo.controller;

import com.plateforme_etudiant.demo.dto.ProfesseurRequestDTO;
import com.plateforme_etudiant.demo.dto.ProfesseurResponseDTO;
import com.plateforme_etudiant.demo.service.ProfesseurService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/professeurs")
public class ProfesseurController {

    private final ProfesseurService professeurService;

    // Constructeur explicite
    public ProfesseurController(ProfesseurService professeurService) {
        this.professeurService = professeurService;
    }

    /**
     * Créer un nouveau professeur (avec son utilisateur)
     * POST /api/professeurs
     */
    @PostMapping
    public ResponseEntity<ProfesseurResponseDTO> creerProfesseur(
            @Valid @RequestBody ProfesseurRequestDTO request) {
        ProfesseurResponseDTO professeur = professeurService.creerProfesseur(request);
        return new ResponseEntity<>(professeur, HttpStatus.CREATED);
    }

    /**
     * Récupérer tous les professeurs
     * GET /api/professeurs
     */
    @GetMapping
    public ResponseEntity<List<ProfesseurResponseDTO>> getAllProfesseurs() {
        List<ProfesseurResponseDTO> professeurs = professeurService.getAllProfesseurs();
        return ResponseEntity.ok(professeurs);
    }

    /**
     * Récupérer un professeur par ID
     * GET /api/professeurs/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProfesseurResponseDTO> getProfesseurById(@PathVariable Long id) {
        ProfesseurResponseDTO professeur = professeurService.getProfesseurById(id);
        return ResponseEntity.ok(professeur);
    }

    /**
     * Récupérer un professeur par ID utilisateur
     * GET /api/professeurs/utilisateur/{utilisateurId}
     */
    @GetMapping("/utilisateur/{utilisateurId}")
    public ResponseEntity<ProfesseurResponseDTO> getProfesseurByUtilisateurId(@PathVariable Long utilisateurId) {
        ProfesseurResponseDTO professeur = professeurService.getProfesseurByUtilisateurId(utilisateurId);
        return ResponseEntity.ok(professeur);
    }

    /**
     * Mettre à jour un professeur
     * PUT /api/professeurs/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<ProfesseurResponseDTO> mettreAJourProfesseur(
            @PathVariable Long id,
            @Valid @RequestBody ProfesseurRequestDTO request) {
        ProfesseurResponseDTO professeur = professeurService.mettreAJourProfesseur(id, request);
        return ResponseEntity.ok(professeur);
    }

    /**
     * Vérifier un professeur
     * PATCH /api/professeurs/{id}/verifier
     */
    @PatchMapping("/{id}/verifier")
    public ResponseEntity<ProfesseurResponseDTO> verifierProfesseur(
            @PathVariable Long id,
            @RequestParam boolean verifie) {
        ProfesseurResponseDTO professeur = professeurService.verifierProfesseur(id, verifie);
        return ResponseEntity.ok(professeur);
    }

    /**
     * Supprimer un professeur
     * DELETE /api/professeurs/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimerProfesseur(@PathVariable Long id) {
        professeurService.supprimerProfesseur(id);
        return ResponseEntity.noContent().build();
    }
}