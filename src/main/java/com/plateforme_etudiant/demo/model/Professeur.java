package com.plateforme_etudiant.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entité représentant un professeur (extension d'un utilisateur)
 */
@Entity
@Table(name = "professeurs")
@NoArgsConstructor
@AllArgsConstructor
public class Professeur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "utilisateur_id", nullable = false, unique = true)
    @JsonIgnore
    private Utilisateur utilisateur;

    @Size(max = 100, message = "La spécialité ne peut pas dépasser 100 caractères")
    @Column(length = 100)
    private String specialite;

    @Column(columnDefinition = "TEXT")
    private String biographie;

    @Column(name = "est_verifie", nullable = false)
    private Boolean verifie = false;

    @CreationTimestamp
    @Column(name = "date_creation", updatable = false)
    private LocalDateTime dateCreation;

    @OneToMany(mappedBy = "professeur", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Cours> cours = new ArrayList<>();

    // ========== GETTERS ==========
    public Long getId() {
        return id;
    }

    public Utilisateur getUtilisateur() {
        return utilisateur;
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

    public List<Cours> getCours() {
        return cours;
    }

    // ========== SETTERS ==========
    public void setId(Long id) {
        this.id = id;
    }

    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
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

    public void setCours(List<Cours> cours) {
        this.cours = cours;
    }
}