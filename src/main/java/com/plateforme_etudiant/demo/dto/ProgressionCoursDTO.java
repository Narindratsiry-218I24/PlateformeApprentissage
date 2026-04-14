package com.plateforme_etudiant.demo.dto;

import java.time.LocalDateTime;

public class ProgressionCoursDTO {
    private Long coursId;
    private String coursTitre;
    private String coursDescriptionCourte;
    private String imageCouverture;
    private Integer progression;
    private Integer contenusCompletes;
    private Integer contenusTotal;
    private LocalDateTime dateDernierAcces;
    private Boolean estTermine;

    // Constructeur par défaut
    public ProgressionCoursDTO() {}

    // Getters
    public Long getCoursId() { return coursId; }
    public String getCoursTitre() { return coursTitre; }
    public String getCoursDescriptionCourte() { return coursDescriptionCourte; }
    public String getImageCouverture() { return imageCouverture; }
    public Integer getProgression() { return progression; }
    public Integer getContenusCompletes() { return contenusCompletes; }
    public Integer getContenusTotal() { return contenusTotal; }
    public LocalDateTime getDateDernierAcces() { return dateDernierAcces; }
    public Boolean getEstTermine() { return estTermine; }

    // Setters
    public void setCoursId(Long coursId) { this.coursId = coursId; }
    public void setCoursTitre(String coursTitre) { this.coursTitre = coursTitre; }
    public void setCoursDescriptionCourte(String coursDescriptionCourte) { this.coursDescriptionCourte = coursDescriptionCourte; }
    public void setImageCouverture(String imageCouverture) { this.imageCouverture = imageCouverture; }
    public void setProgression(Integer progression) { this.progression = progression; }
    public void setContenusCompletes(Integer contenusCompletes) { this.contenusCompletes = contenusCompletes; }
    public void setContenusTotal(Integer contenusTotal) { this.contenusTotal = contenusTotal; }
    public void setDateDernierAcces(LocalDateTime dateDernierAcces) { this.dateDernierAcces = dateDernierAcces; }
    public void setEstTermine(Boolean estTermine) { this.estTermine = estTermine; }
}