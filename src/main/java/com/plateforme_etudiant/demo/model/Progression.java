package com.plateforme_etudiant.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "progressions",
        uniqueConstraints = @UniqueConstraint(columnNames = {"apprenant_id", "contenu_item_id"}))
public class Progression {

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

    @Column(name = "est_complete", nullable = false)
    private Boolean estComplete = false;

    @Column(name = "temps_passe")
    private Integer tempsPasse;

    @Column(name = "derniere_position")
    private Integer dernierePosition;

    @CreationTimestamp
    @Column(name = "date_debut", updatable = false)
    private LocalDateTime dateDebut;

    @UpdateTimestamp
    @Column(name = "date_derniere_activite")
    private LocalDateTime dateDerniereActivite;

    @Column(name = "date_completion")
    private LocalDateTime dateCompletion;

    // ========== CONSTRUCTEURS ==========
    public Progression() {}

    public Progression(Long id, Utilisateur apprenant, ContenuItem contenuItem, Boolean estComplete,
                       Integer tempsPasse, Integer dernierePosition, LocalDateTime dateDebut,
                       LocalDateTime dateDerniereActivite, LocalDateTime dateCompletion) {
        this.id = id;
        this.apprenant = apprenant;
        this.contenuItem = contenuItem;
        this.estComplete = estComplete;
        this.tempsPasse = tempsPasse;
        this.dernierePosition = dernierePosition;
        this.dateDebut = dateDebut;
        this.dateDerniereActivite = dateDerniereActivite;
        this.dateCompletion = dateCompletion;
    }

    // ========== GETTERS ==========
    public Long getId() { return id; }
    public Utilisateur getApprenant() { return apprenant; }
    public ContenuItem getContenuItem() { return contenuItem; }
    public Boolean getEstComplete() { return estComplete; }
    public Integer getTempsPasse() { return tempsPasse; }
    public Integer getDernierePosition() { return dernierePosition; }
    public LocalDateTime getDateDebut() { return dateDebut; }
    public LocalDateTime getDateDerniereActivite() { return dateDerniereActivite; }
    public LocalDateTime getDateCompletion() { return dateCompletion; }


    // ========== SETTERS ==========
    public void setId(Long id) { this.id = id; }
    public void setApprenant(Utilisateur apprenant) { this.apprenant = apprenant; }
    public void setContenuItem(ContenuItem contenuItem) { this.contenuItem = contenuItem; }
    public void setEstComplete(Boolean estComplete) { this.estComplete = estComplete; }
    public void setTempsPasse(Integer tempsPasse) { this.tempsPasse = tempsPasse; }
    public void setDernierePosition(Integer dernierePosition) { this.dernierePosition = dernierePosition; }
    public void setDateDebut(LocalDateTime dateDebut) { this.dateDebut = dateDebut; }
    public void setDateDerniereActivite(LocalDateTime dateDerniereActivite) { this.dateDerniereActivite = dateDerniereActivite; }
    public void setDateCompletion(LocalDateTime dateCompletion) { this.dateCompletion = dateCompletion; }

    // ========== MÉTHODES UTILITAIRES ==========
    public void completer() {
        this.estComplete = true;
        this.dateCompletion = LocalDateTime.now();
    }

    public void updateTempsPasse(int secondes) {
        this.tempsPasse = secondes;
        this.dateDerniereActivite = LocalDateTime.now();
    }

    public Integer getPourcentageProgression() {
        if (contenuItem != null && contenuItem.estVideo() && contenuItem.getDureeVideo() != null
                && contenuItem.getDureeVideo() > 0 && tempsPasse != null) {
            return Math.min(100, (tempsPasse * 100) / contenuItem.getDureeVideo());
        }
        return estComplete != null && estComplete ? 100 : 0;
    }
}