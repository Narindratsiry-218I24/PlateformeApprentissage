package com.plateforme_etudiant.demo.dto;

import java.time.LocalDateTime;

public class CoursEtudiantDTO {
    private Long id;
    private String titre;
    private String descriptionCourte;
    private String imageCouverture;
    private Boolean publie;
    private Integer dureeEstimee;
    private Integer progression;
    private String professeur;
    private LocalDateTime derniereActivite;
    private Integer nombreChapitresCompletes;
    private Integer nombreChapitresTotal;

    public CoursEtudiantDTO() {}

    // Getters
    public Long getId() { return id; }
    public String getTitre() { return titre; }
    public String getDescriptionCourte() { return descriptionCourte; }
    public String getImageCouverture() { return imageCouverture; }
    public Boolean getPublie() { return publie; }
    public Integer getDureeEstimee() { return dureeEstimee; }
    public Integer getProgression() { return progression; }
    public String getProfesseur() { return professeur; }
    public LocalDateTime getDerniereActivite() { return derniereActivite; }
    public Integer getNombreChapitresCompletes() { return nombreChapitresCompletes; }
    public Integer getNombreChapitresTotal() { return nombreChapitresTotal; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setTitre(String titre) { this.titre = titre; }
    public void setDescriptionCourte(String descriptionCourte) { this.descriptionCourte = descriptionCourte; }
    public void setImageCouverture(String imageCouverture) { this.imageCouverture = imageCouverture; }
    public void setPublie(Boolean publie) { this.publie = publie; }
    public void setDureeEstimee(Integer dureeEstimee) { this.dureeEstimee = dureeEstimee; }
    public void setProgression(Integer progression) { this.progression = progression; }
    public void setProfesseur(String professeur) { this.professeur = professeur; }
    public void setDerniereActivite(LocalDateTime derniereActivite) { this.derniereActivite = derniereActivite; }
    public void setNombreChapitresCompletes(Integer nombreChapitresCompletes) { this.nombreChapitresCompletes = nombreChapitresCompletes; }
    public void setNombreChapitresTotal(Integer nombreChapitresTotal) { this.nombreChapitresTotal = nombreChapitresTotal; }
}