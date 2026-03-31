package com.plateforme_etudiant.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "favoris",
        uniqueConstraints = @UniqueConstraint(columnNames = {"apprenant_id", "contenu_item_id"}))
@NoArgsConstructor
@AllArgsConstructor
public class Favori {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "apprenant_id", nullable = false)
    @JsonIgnore
    private Utilisateur apprenant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contenu_item_id", nullable = false)
    @JsonIgnore
    private ContenuItem contenuItem;

    @CreationTimestamp
    @Column(name = "date_creation", updatable = false)
    private LocalDateTime dateCreation;

    // ========== GETTERS ==========
    public Long getId() {
        return id;
    }

    public Utilisateur getApprenant() {
        return apprenant;
    }

    public ContenuItem getContenuItem() {
        return contenuItem;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    // ========== SETTERS ==========
    public void setId(Long id) {
        this.id = id;
    }

    public void setApprenant(Utilisateur apprenant) {
        this.apprenant = apprenant;
    }

    public void setContenuItem(ContenuItem contenuItem) {
        this.contenuItem = contenuItem;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }

    // ========== MÉTHODES UTILITAIRES AVEC GESTION DES NULLS ==========

    /**
     * Récupère le titre du contenu favori
     */
    public String getTitreContenu() {
        if (contenuItem == null) {
            return null;
        }
        return contenuItem.getTitre();
    }

    /**
     * Récupère le type de contenu (VIDEO, TEXTE, etc.)
     */
    public String getTypeContenu() {
        if (contenuItem == null || contenuItem.getTypeContenu() == null) {
            return null;
        }
        return contenuItem.getTypeContenu().toString();
    }

    /**
     * Récupère le titre du chapitre du contenu favori
     */
    public String getTitreChapitre() {
        if (contenuItem == null) {
            return null;
        }
        Chapitre chapitre = contenuItem.getChapitre();
        if (chapitre == null) {
            return null;
        }
        return chapitre.getTitre();
    }

    /**
     * Récupère le titre du cours du contenu favori
     */
    public String getTitreCours() {
        if (contenuItem == null) {
            return null;
        }
        Chapitre chapitre = contenuItem.getChapitre();
        if (chapitre == null) {
            return null;
        }
        Section section = chapitre.getSection();
        if (section == null) {
            return null;
        }
        Cours cours = section.getCours();
        if (cours == null) {
            return null;
        }
        return cours.getTitre();
    }

    /**
     * Récupère l'ID du cours
     */
    public Long getCoursId() {
        if (contenuItem == null) {
            return null;
        }
        Chapitre chapitre = contenuItem.getChapitre();
        if (chapitre == null) {
            return null;
        }
        Section section = chapitre.getSection();
        if (section == null) {
            return null;
        }
        Cours cours = section.getCours();
        if (cours == null) {
            return null;
        }
        return cours.getId();
    }

    /**
     * Récupère l'ID du chapitre
     */
    public Long getChapitreId() {
        if (contenuItem == null) {
            return null;
        }
        Chapitre chapitre = contenuItem.getChapitre();
        if (chapitre == null) {
            return null;
        }
        return chapitre.getId();
    }

    /**
     * Récupère le nom de l'apprenant
     */
    public String getNomApprenant() {
        if (apprenant == null) {
            return null;
        }
        return apprenant.getNomComplet();
    }

    /**
     * Récupère l'URL du contenu (pour redirection)
     */
    public String getUrlContenu() {
        if (contenuItem == null) {
            return null;
        }

        Long coursId = getCoursId();
        Long chapitreId = getChapitreId();
        Long contenuId = contenuItem.getId();

        if (coursId == null || chapitreId == null || contenuId == null) {
            return null;
        }

        switch (contenuItem.getTypeContenu()) {
            case VIDEO:
                return "/cours/" + coursId + "/chapitres/" + chapitreId + "/contenus/" + contenuId + "/video";
            case TEXTE:
                return "/cours/" + coursId + "/chapitres/" + chapitreId + "/contenus/" + contenuId + "/texte";
            case PDF:
                return "/cours/" + coursId + "/chapitres/" + chapitreId + "/contenus/" + contenuId + "/pdf";
            default:
                return "/cours/" + coursId + "/chapitres/" + chapitreId + "/contenus/" + contenuId;
        }
    }
}