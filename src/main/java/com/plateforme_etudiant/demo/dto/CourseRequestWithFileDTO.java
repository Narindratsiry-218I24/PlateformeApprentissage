package com.plateforme_etudiant.demo.dto;

import org.springframework.web.multipart.MultipartFile;

public class CourseRequestWithFileDTO {
    private String titre;
    private String descriptionCourte;
    private String description;
    private MultipartFile imageCouverture;  // Fichier image au lieu d'URL
    private Integer dureeEstimee;
    private String sections;  // JSON des sections

    // Getters et Setters
    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }

    public String getDescriptionCourte() { return descriptionCourte; }
    public void setDescriptionCourte(String descriptionCourte) { this.descriptionCourte = descriptionCourte; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public MultipartFile getImageCouverture() { return imageCouverture; }
    public void setImageCouverture(MultipartFile imageCouverture) { this.imageCouverture = imageCouverture; }

    public Integer getDureeEstimee() { return dureeEstimee; }
    public void setDureeEstimee(Integer dureeEstimee) { this.dureeEstimee = dureeEstimee; }

    public String getSections() { return sections; }
    public void setSections(String sections) { this.sections = sections; }
}