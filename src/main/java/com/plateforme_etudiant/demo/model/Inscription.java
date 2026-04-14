package com.plateforme_etudiant.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "inscriptions",
        uniqueConstraints = @UniqueConstraint(columnNames = {"apprenant_id", "cours_id"}))
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

    @Transient
    private Integer progressionPourcentage;

    // ========== CONSTRUCTEURS ==========
    public Inscription() {}

    public Inscription(Long id, Utilisateur apprenant, Cours cours, LocalDateTime dateInscription,
                       LocalDateTime dateDernierAcces, LocalDateTime dateAchevement,
                       Boolean termine, Boolean favori, String notesPersonnelles) {
        this.id = id;
        this.apprenant = apprenant;
        this.cours = cours;
        this.dateInscription = dateInscription;
        this.dateDernierAcces = dateDernierAcces;
        this.dateAchevement = dateAchevement;
        this.termine = termine;
        this.favori = favori;
        this.notesPersonnelles = notesPersonnelles;
    }

    // ========== GETTERS ==========
    public Long getId() { return id; }
    public Utilisateur getApprenant() { return apprenant; }
    public Cours getCours() { return cours; }
    public LocalDateTime getDateInscription() { return dateInscription; }
    public LocalDateTime getDateDernierAcces() { return dateDernierAcces; }
    public LocalDateTime getDateAchevement() { return dateAchevement; }
    public Boolean getTermine() { return termine; }
    public Boolean getFavori() { return favori; }
    public String getNotesPersonnelles() { return notesPersonnelles; }
    public Integer getProgressionPourcentage() { return progressionPourcentage; }

    // ========== SETTERS ==========
    public void setId(Long id) { this.id = id; }
    public void setApprenant(Utilisateur apprenant) { this.apprenant = apprenant; }
    public void setCours(Cours cours) { this.cours = cours; }
    public void setDateInscription(LocalDateTime dateInscription) { this.dateInscription = dateInscription; }
    public void setDateDernierAcces(LocalDateTime dateDernierAcces) { this.dateDernierAcces = dateDernierAcces; }
    public void setDateAchevement(LocalDateTime dateAchevement) { this.dateAchevement = dateAchevement; }
    public void setTermine(Boolean termine) { this.termine = termine; }
    public void setFavori(Boolean favori) { this.favori = favori; }
    public void setNotesPersonnelles(String notesPersonnelles) { this.notesPersonnelles = notesPersonnelles; }
    public void setProgressionPourcentage(Integer progressionPourcentage) { this.progressionPourcentage = progressionPourcentage; }

    // ========== MÉTHODES UTILITAIRES ==========
    public void mettreAJourAcces() {
        this.dateDernierAcces = LocalDateTime.now();
    }

    public void terminer() {
        this.termine = true;
        this.dateAchevement = LocalDateTime.now();
    }
}