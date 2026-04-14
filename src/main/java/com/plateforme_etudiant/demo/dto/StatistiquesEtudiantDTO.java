// dto/StatistiquesEtudiantDTO.java
package com.plateforme_etudiant.demo.dto;

public class StatistiquesEtudiantDTO {
    private Integer totalCours;
    private Integer totalHeures;
    private Integer progressionMoyenne;
    private Integer certificats;

    public StatistiquesEtudiantDTO() {}

    public StatistiquesEtudiantDTO(Integer totalCours, Integer totalHeures, Integer progressionMoyenne, Integer certificats) {
        this.totalCours = totalCours;
        this.totalHeures = totalHeures;
        this.progressionMoyenne = progressionMoyenne;
        this.certificats = certificats;
    }

    // Getters et Setters
    public Integer getTotalCours() { return totalCours; }
    public void setTotalCours(Integer totalCours) { this.totalCours = totalCours; }
    public Integer getTotalHeures() { return totalHeures; }
    public void setTotalHeures(Integer totalHeures) { this.totalHeures = totalHeures; }
    public Integer getProgressionMoyenne() { return progressionMoyenne; }
    public void setProgressionMoyenne(Integer progressionMoyenne) { this.progressionMoyenne = progressionMoyenne; }
    public Integer getCertificats() { return certificats; }
    public void setCertificats(Integer certificats) { this.certificats = certificats; }
}