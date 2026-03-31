package com.plateforme_etudiant.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entité représentant l'inscription d'un apprenant à un cours
 */
@Entity
@Table(name = "inscriptions",
        uniqueConstraints = @UniqueConstraint(columnNames = {"apprenant_id", "cours_id"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Inscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "apprenant_id", nullable = false)
    @JsonIgnore
    private Utilisateur apprenant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cours_id", nullable = false)
    @JsonIgnore
    private Cours cours;

    @CreationTimestamp
    @Column(name = "date_inscription", updatable = false)
    private LocalDateTime dateInscription;

    @Column(name = "date_dernier_acces")
    private LocalDateTime dateDernierAcces;

    @Column(name = "date_achevement")
    private LocalDateTime dateAchevement;

    @Column(name = "est_termine", nullable = false)
    private Boolean termine = false;

    @Column(name = "est_favori", nullable = false)
    private Boolean favori = false;

    @Column(name = "notes_personnelles", columnDefinition = "TEXT")
    private String notesPersonnelles;

    /**
     * Met à jour la date de dernier accès
     */
    public void mettreAJourAcces() {
        this.dateDernierAcces = LocalDateTime.now();
    }

    /**
     * Marque le cours comme terminé
     */
    public void terminer() {
        this.termine = true;
        this.dateAchevement = LocalDateTime.now();
    }

    /**
     * Calcule la progression en pourcentage (sera mis à jour par un service)
     */
    @Transient
    private Integer progressionPourcentage;



}
