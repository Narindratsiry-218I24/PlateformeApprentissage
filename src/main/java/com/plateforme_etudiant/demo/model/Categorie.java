// Categorie.java
package com.plateforme_etudiant.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "categories")
@NoArgsConstructor
@AllArgsConstructor
public class Categorie implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le nom de la catégorie est obligatoire")
    @Size(max = 100, message = "Le nom ne peut pas dépasser 100 caractères")
    @Column(length = 100, nullable = false)
    private String nom;

    @NotBlank(message = "Le slug est obligatoire")
    @Size(max = 100, message = "Le slug ne peut pas dépasser 100 caractères")
    @Column(length = 100, nullable = false, unique = true)
    private String slug;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    @JsonIgnore
    private Categorie parent;

    @Column(name = "est_active", nullable = false)
    private Boolean active = true;

    @CreationTimestamp
    @Column(name = "date_creation", updatable = false)
    private LocalDateTime dateCreation;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Categorie> sousCategories = new ArrayList<>();

    @OneToMany(mappedBy = "categorie", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Cours> cours = new ArrayList<>();

    // ========== GETTERS ==========
    public Long getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public String getSlug() {
        return slug;
    }

    public String getDescription() {
        return description;
    }

    public Categorie getParent() {
        return parent;
    }

    public Boolean getActive() {
        return active;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public List<Categorie> getSousCategories() {
        return sousCategories;
    }

    public List<Cours> getCours() {
        return cours;
    }

    // ========== SETTERS ==========
    public void setId(Long id) {
        this.id = id;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setParent(Categorie parent) {
        this.parent = parent;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }

    public void setSousCategories(List<Categorie> sousCategories) {
        this.sousCategories = sousCategories;
    }

    public void setCours(List<Cours> cours) {
        this.cours = cours;
    }
}