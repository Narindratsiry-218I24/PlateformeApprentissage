package com.plateforme_etudiant.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entité représentant la progression d'un apprenant sur un contenu
 */
@Entity
@Table(name = "progressions",
        uniqueConstraints = @UniqueConstraint(columnNames = {"apprenant_id", "contenu_item_id"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Progression {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "apprenant_id", nullable = false)
    @JsonIgnore
    private Utilisateur apprenant;

    // MODIFICATION : Pointer vers ContenuItem au lieu de Lecon
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contenu_item_id", nullable = false)
    @JsonIgnore
    private ContenuItem contenuItem;

    @Column(name = "est_complete", nullable = false)
    private Boolean estComplete = false;

    @Column(name = "temps_passe")
    private Integer tempsPasse; // en secondes

    @Column(name = "derniere_position")
    private Integer dernierePosition; // pour les vidéos (position en secondes)

    @CreationTimestamp
    @Column(name = "date_debut", updatable = false)
    private LocalDateTime dateDebut;

    @UpdateTimestamp
    @Column(name = "date_derniere_activite")
    private LocalDateTime dateDerniereActivite;

    @Column(name = "date_completion")
    private LocalDateTime dateCompletion;

    /**
     * Marquer le contenu comme complété
     */
    public void completer() {
        this.estComplete = true;
        this.dateCompletion = LocalDateTime.now();
    }

    /**
     * Mettre à jour le temps passé
     */
    public void updateTempsPasse(int secondes) {
        this.tempsPasse = secondes;
        this.dateDerniereActivite = LocalDateTime.now();
    }

    /**
     * Calculer le pourcentage de progression
     * Pour une vidéo, basé sur la durée
     */
    public Integer getPourcentageProgression() {
        if (contenuItem != null && contenuItem.estVideo() && contenuItem.getDureeVideo() != null
                && contenuItem.getDureeVideo() > 0 && tempsPasse != null) {
            return Math.min(100, (tempsPasse * 100) / contenuItem.getDureeVideo());
        }
        return estComplete ? 100 : 0;
    }
}