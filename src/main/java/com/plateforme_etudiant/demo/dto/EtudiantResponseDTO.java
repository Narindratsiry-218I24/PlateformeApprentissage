package com.plateforme_etudiant.demo.dto;

import java.time.LocalDateTime;

public class EtudiantResponseDTO {
    private Long id;
    private Long utilisateurId;
    private String nomUtilisateur;
    private String email;
    private String prenom;
    private String nom;
    private String nomComplet;
    private String niveau;
    private LocalDateTime dateCreation;
    private Integer nombreCoursInscrits;
    private Integer heuresTotalEtudiees;
    private Double progressionMoyenne;

    // Constructeurs
    public EtudiantResponseDTO() {}

    // Getters
    public Long getId() { return id; }
    public Long getUtilisateurId() { return utilisateurId; }
    public String getNomUtilisateur() { return nomUtilisateur; }
    public String getEmail() { return email; }
    public String getPrenom() { return prenom; }
    public String getNom() { return nom; }
    public String getNomComplet() { return nomComplet; }
    public String getNiveau() { return niveau; }
    public LocalDateTime getDateCreation() { return dateCreation; }
    public Integer getNombreCoursInscrits() { return nombreCoursInscrits; }
    public Integer getHeuresTotalEtudiees() { return heuresTotalEtudiees; }
    public Double getProgressionMoyenne() { return progressionMoyenne; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setUtilisateurId(Long utilisateurId) { this.utilisateurId = utilisateurId; }
    public void setNomUtilisateur(String nomUtilisateur) { this.nomUtilisateur = nomUtilisateur; }
    public void setEmail(String email) { this.email = email; }
    public void setPrenom(String prenom) { this.prenom = prenom; }
    public void setNom(String nom) { this.nom = nom; }
    public void setNomComplet(String nomComplet) { this.nomComplet = nomComplet; }
    public void setNiveau(String niveau) { this.niveau = niveau; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }
    public void setNombreCoursInscrits(Integer nombreCoursInscrits) { this.nombreCoursInscrits = nombreCoursInscrits; }
    public void setHeuresTotalEtudiees(Integer heuresTotalEtudiees) { this.heuresTotalEtudiees = heuresTotalEtudiees; }
    public void setProgressionMoyenne(Double progressionMoyenne) { this.progressionMoyenne = progressionMoyenne; }
}