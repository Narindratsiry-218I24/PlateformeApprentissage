// Utilisateur.java
package com.plateforme_etudiant.demo.model;

import com.plateforme_etudiant.demo.model.enums.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "utilisateurs")
@NoArgsConstructor
@AllArgsConstructor
public class Utilisateur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le nom d'utilisateur est obligatoire")
    @Size(min = 3, max = 50, message = "Le nom d'utilisateur doit contenir entre 3 et 50 caractères")
    @Column(name = "nom_utilisateur", length = 50, nullable = false, unique = true)
    private String nomUtilisateur;

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "Format d'email invalide")
    @Size(max = 100, message = "L'email ne peut pas dépasser 100 caractères")
    @Column(length = 100, nullable = false, unique = true)
    private String email;

    @NotBlank(message = "Le mot de passe est obligatoire")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(name = "mot_de_passe", length = 60, nullable = false)
    private String motDePasse;

    @Size(max = 50, message = "Le prénom ne peut pas dépasser 50 caractères")
    @Column(length = 50)
    private String prenom;

    @Size(max = 50, message = "Le nom ne peut pas dépasser 50 caractères")
    @Column(length = 50)
    private String nom;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private Role role = Role.APPRENANT;

    @Column(name = "est_actif", nullable = false)
    private Boolean actif = true;

    @CreationTimestamp
    @Column(name = "date_creation", updatable = false)
    private LocalDateTime dateCreation;

    @UpdateTimestamp
    @Column(name = "date_modification")
    private LocalDateTime dateModification;

    // Relations
    @OneToOne(mappedBy = "utilisateur", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private Professeur professeur;

    @OneToMany(mappedBy = "apprenant", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Inscription> inscriptions = new ArrayList<>();

    @OneToMany(mappedBy = "apprenant", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Progression> progressions = new ArrayList<>();

    @OneToMany(mappedBy = "apprenant", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Favori> favoris = new ArrayList<>();

    // ========== GETTERS ==========
    public Long getId() {
        return id;
    }

    public String getNomUtilisateur() {
        return nomUtilisateur;
    }

    public String getEmail() {
        return email;
    }

    public String getMotDePasse() {
        return motDePasse;
    }

    public String getPrenom() {
        return prenom;
    }

    public String getNom() {
        return nom;
    }

    public Role getRole() {
        return role;
    }

    public Boolean getActif() {
        return actif;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public LocalDateTime getDateModification() {
        return dateModification;
    }

    public Professeur getProfesseur() {
        return professeur;
    }

    public List<Inscription> getInscriptions() {
        return inscriptions;
    }

    public List<Progression> getProgressions() {
        return progressions;
    }

    public List<Favori> getFavoris() {
        return favoris;
    }

    // ========== SETTERS ==========
    public void setId(Long id) {
        this.id = id;
    }

    public void setNomUtilisateur(String nomUtilisateur) {
        this.nomUtilisateur = nomUtilisateur;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setMotDePasse(String motDePasse) {
        this.motDePasse = motDePasse;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public void setActif(Boolean actif) {
        this.actif = actif;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }

    public void setDateModification(LocalDateTime dateModification) {
        this.dateModification = dateModification;
    }

    public void setProfesseur(Professeur professeur) {
        this.professeur = professeur;
    }

    public void setInscriptions(List<Inscription> inscriptions) {
        this.inscriptions = inscriptions;
    }

    public void setProgressions(List<Progression> progressions) {
        this.progressions = progressions;
    }

    public void setFavoris(List<Favori> favoris) {
        this.favoris = favoris;
    }

    // ========== MÉTHODES UTILITAIRES ==========

    /**
     * Retourne le nom complet (prénom + nom)
     */
    public String getNomComplet() {
        if (prenom != null && nom != null) {
            return prenom + " " + nom;
        } else if (prenom != null) {
            return prenom;
        } else if (nom != null) {
            return nom;
        }
        return nomUtilisateur;
    }

    /**
     * Vérifie si l'utilisateur est un administrateur
     */
    public boolean estAdministrateur() {
        return Role.ADMINISTRATEUR.equals(this.role);
    }

    public boolean estProfesseur() {
        return Role.PROFESSEUR.equals(this.role);
    }

    public boolean estApprenant() {
        return Role.APPRENANT.equals(this.role);
    }
}