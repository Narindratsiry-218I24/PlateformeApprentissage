package com.plateforme_etudiant.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cours")
@NoArgsConstructor
@AllArgsConstructor
public class Cours {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le titre du cours est obligatoire")
    @Size(max = 200, message = "Le titre ne peut pas dépasser 200 caractères")
    @Column(length = 200, nullable = false)
    private String titre;

    @NotBlank(message = "Le slug est obligatoire")
    @Size(max = 200, message = "Le slug ne peut pas dépasser 200 caractères")
    @Column(length = 200, nullable = false, unique = true)
    private String slug;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Size(max = 300, message = "La description courte ne peut pas dépasser 300 caractères")
    @Column(name = "description_courte", length = 300)
    private String descriptionCourte;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categorie_id")
    @JsonIgnore
    private Categorie categorie;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "professeur_id", nullable = false)
    @JsonIgnore
    private Professeur professeur;

    @Column(name = "image_couverture", length = 255)
    private String imageCouverture;

    @Column(name = "est_publie", nullable = false)
    private Boolean publie = false;

    @Column(name = "duree_estimee")
    private Integer dureeEstimee;

    @Column(name = "nombre_apprenants", nullable = false)
    private Integer nombreApprenants = 0;

    @CreationTimestamp
    @Column(name = "date_creation", updatable = false)
    private LocalDateTime dateCreation;

    @Column(name = "date_publication")
    private LocalDateTime datePublication;

    @OneToMany(mappedBy = "cours", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Section> sections = new ArrayList<>();

    @OneToMany(mappedBy = "cours", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Inscription> inscriptions = new ArrayList<>();

    // ========== GETTERS ==========
    public Long getId() {
        return id;
    }

    public String getTitre() {
        return titre;
    }

    public String getSlug() {
        return slug;
    }

    public String getDescription() {
        return description;
    }

    public String getDescriptionCourte() {
        return descriptionCourte;
    }

    public Categorie getCategorie() {
        return categorie;
    }

    public Professeur getProfesseur() {
        return professeur;
    }

    public String getImageCouverture() {
        return imageCouverture;
    }

    public Boolean getPublie() {
        return publie;
    }

    public Integer getDureeEstimee() {
        return dureeEstimee;
    }

    public Integer getNombreApprenants() {
        return nombreApprenants;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public LocalDateTime getDatePublication() {
        return datePublication;
    }

    public List<Section> getSections() {
        return sections;
    }

    public List<Inscription> getInscriptions() {
        return inscriptions;
    }

    // ========== SETTERS ==========
    public void setId(Long id) {
        this.id = id;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDescriptionCourte(String descriptionCourte) {
        this.descriptionCourte = descriptionCourte;
    }

    public void setCategorie(Categorie categorie) {
        this.categorie = categorie;
    }

    public void setProfesseur(Professeur professeur) {
        this.professeur = professeur;
    }

    public void setImageCouverture(String imageCouverture) {
        this.imageCouverture = imageCouverture;
    }

    public void setPublie(Boolean publie) {
        this.publie = publie;
    }

    public void setDureeEstimee(Integer dureeEstimee) {
        this.dureeEstimee = dureeEstimee;
    }

    public void setNombreApprenants(Integer nombreApprenants) {
        this.nombreApprenants = nombreApprenants;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }

    public void setDatePublication(LocalDateTime datePublication) {
        this.datePublication = datePublication;
    }

    public void setSections(List<Section> sections) {
        this.sections = sections;
    }

    public void setInscriptions(List<Inscription> inscriptions) {
        this.inscriptions = inscriptions;
    }

    // ========== MÉTHODES UTILITAIRES ==========
    public void incrementerApprenants() {
        this.nombreApprenants++;
    }

    public void decrementerApprenants() {
        if (this.nombreApprenants > 0) {
            this.nombreApprenants--;
        }
    }

    public boolean estPublie() {
        return publie != null && publie;
    }

    public void publier() {
        this.publie = true;
        this.datePublication = LocalDateTime.now();
    }

    public void depublier() {
        this.publie = false;
        this.datePublication = null;
    }
}