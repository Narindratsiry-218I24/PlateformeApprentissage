// ProfesseurResponseDTO.java
package com.plateforme_etudiant.demo.dto;

import java.time.LocalDateTime;

public class ProfesseurResponseDTO {

    private Long id;
    private Long utilisateurId;
    private String nomUtilisateur;
    private String email;
    private String prenom;
    private String nom;
    private String nomComplet;
    private String specialite;
    private String biographie;
    private Boolean verifie;
    private LocalDateTime dateCreation;
    private Long nombreCours;

    // ========== GETTERS ==========
    public Long getId() {
        return id;
    }

    public Long getUtilisateurId() {
        return utilisateurId;
    }

    public String getNomUtilisateur() {
        return nomUtilisateur;
    }

    public String getEmail() {
        return email;
    }

    public String getPrenom() {
        return prenom;
    }

    public String getNom() {
        return nom;
    }

    public String getNomComplet() {
        return nomComplet;
    }

    public String getSpecialite() {
        return specialite;
    }

    public String getBiographie() {
        return biographie;
    }

    public Boolean getVerifie() {
        return verifie;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public Long getNombreCours() {
        return nombreCours;
    }

    // ========== SETTERS ==========
    public void setId(Long id) {
        this.id = id;
    }

    public void setUtilisateurId(Long utilisateurId) {
        this.utilisateurId = utilisateurId;
    }

    public void setNomUtilisateur(String nomUtilisateur) {
        this.nomUtilisateur = nomUtilisateur;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setNomComplet(String nomComplet) {
        this.nomComplet = nomComplet;
    }

    public void setSpecialite(String specialite) {
        this.specialite = specialite;
    }

    public void setBiographie(String biographie) {
        this.biographie = biographie;
    }

    public void setVerifie(Boolean verifie) {
        this.verifie = verifie;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }

    public void setNombreCours(Long nombreCours) {
        this.nombreCours = nombreCours;
    }
}