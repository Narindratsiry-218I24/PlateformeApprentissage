// dto/ProgressionMatiereDTO.java
package com.plateforme_etudiant.demo.dto;

public class ProgressionMatiereDTO {
    private String nom;
    private String description;
    private Integer progression;

    public ProgressionMatiereDTO() {}

    public ProgressionMatiereDTO(String nom, String description, Integer progression) {
        this.nom = nom;
        this.description = description;
        this.progression = progression;
    }

    // Getters et Setters
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Integer getProgression() { return progression; }
    public void setProgression(Integer progression) { this.progression = progression; }
}