package com.plateforme_etudiant.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.plateforme_etudiant.demo.model.enums.TypeContenu;
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
@Table(name = "chapitres")
@NoArgsConstructor
@AllArgsConstructor
public class Chapitre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "section_id", nullable = false)
    @JsonIgnore
    private Section section;

    @NotBlank(message = "Le titre du chapitre est obligatoire")
    @Size(max = 200, message = "Le titre ne peut pas dépasser 200 caractères")
    @Column(length = 200, nullable = false)
    private String titre;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private Integer ordre = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_chapitre_id")
    @JsonIgnore
    private Chapitre parentChapitre;

    @OneToMany(mappedBy = "parentChapitre", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Chapitre> sousChapitres = new ArrayList<>();

    @OneToMany(mappedBy = "chapitre", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @OrderBy("ordre ASC")
    private List<ContenuItem> contenus = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "date_creation", updatable = false)
    private LocalDateTime dateCreation;

    // ========== GETTERS ==========
    public Long getId() {
        return id;
    }

    public Section getSection() {
        return section;
    }

    public String getTitre() {
        return titre;
    }

    public String getDescription() {
        return description;
    }

    public Integer getOrdre() {
        return ordre;
    }

    public Chapitre getParentChapitre() {
        return parentChapitre;
    }

    public List<Chapitre> getSousChapitres() {
        return sousChapitres;
    }

    public List<ContenuItem> getContenus() {
        return contenus;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    // ========== SETTERS ==========
    public void setId(Long id) {
        this.id = id;
    }

    public void setSection(Section section) {
        this.section = section;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setOrdre(Integer ordre) {
        this.ordre = ordre;
    }

    public void setParentChapitre(Chapitre parentChapitre) {
        this.parentChapitre = parentChapitre;
    }

    public void setSousChapitres(List<Chapitre> sousChapitres) {
        this.sousChapitres = sousChapitres;
    }

    public void setContenus(List<ContenuItem> contenus) {
        this.contenus = contenus;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }

    // ========== MÉTHODES UTILITAIRES ==========
    public void ajouterContenu(ContenuItem contenu) {
        contenus.add(contenu);
        contenu.setChapitre(this);
        contenu.setSection(this.section);
    }

    public List<ContenuItem> getVideos() {
        return contenus.stream()
                .filter(c -> c.getTypeContenu() == TypeContenu.VIDEO)
                .collect(java.util.stream.Collectors.toList());
    }

    public List<ContenuItem> getTextes() {
        return contenus.stream()
                .filter(c -> c.getTypeContenu() == TypeContenu.TEXTE)
                .collect(java.util.stream.Collectors.toList());
    }

    public List<ContenuItem> getFichiers() {
        return contenus.stream()
                .filter(c -> c.getTypeContenu() == TypeContenu.PDF ||
                        c.getTypeContenu() == TypeContenu.PRESENTATION)
                .collect(java.util.stream.Collectors.toList());
    }

    public List<ContenuItem> getLiens() {
        return contenus.stream()
                .filter(c -> c.getTypeContenu() == TypeContenu.LIEN)
                .collect(java.util.stream.Collectors.toList());
    }

    public boolean aDesSousChapitres() {
        return sousChapitres != null && !sousChapitres.isEmpty();
    }

    public boolean estSousChapitre() {
        return parentChapitre != null;
    }

    public int getNiveauProfondeur() {
        int niveau = 0;
        Chapitre courant = this;
        while (courant.parentChapitre != null) {
            niveau++;
            courant = courant.parentChapitre;
        }
        return niveau;
    }

    public int getNombreTotalContenus() {
        int total = contenus.size();
        for (Chapitre sousChapitre : sousChapitres) {
            total += sousChapitre.getNombreTotalContenus();
        }
        return total;
    }
}