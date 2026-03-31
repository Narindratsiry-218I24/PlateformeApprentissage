// CourseRequestDTO.java
package com.plateforme_etudiant.demo.dto;

import java.util.ArrayList;
import java.util.List;

public class CourseRequestDTO {

    private String titre;
    private String descriptionCourte;
    private String description;
    private Long categorieId;
    private String imageCouverture;
    private Integer dureeEstimee;
    private List<SectionDTO> sections = new ArrayList<>();

    // ========== GETTERS ==========
    public String getTitre() {
        return titre;
    }

    public String getDescriptionCourte() {
        return descriptionCourte;
    }

    public String getDescription() {
        return description;
    }

    public Long getCategorieId() {
        return categorieId;
    }

    public String getImageCouverture() {
        return imageCouverture;
    }

    public Integer getDureeEstimee() {
        return dureeEstimee;
    }

    public List<SectionDTO> getSections() {
        return sections;
    }

    // ========== SETTERS ==========
    public void setTitre(String titre) {
        this.titre = titre;
    }

    public void setDescriptionCourte(String descriptionCourte) {
        this.descriptionCourte = descriptionCourte;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCategorieId(Long categorieId) {
        this.categorieId = categorieId;
    }

    public void setImageCouverture(String imageCouverture) {
        this.imageCouverture = imageCouverture;
    }

    public void setDureeEstimee(Integer dureeEstimee) {
        this.dureeEstimee = dureeEstimee;
    }

    public void setSections(List<SectionDTO> sections) {
        this.sections = sections;
    }

    // ========== CLASSES INTERNES ==========

    public static class SectionDTO {
        private String titre;
        private String description;
        private Integer ordre;
        private List<ChapitreDTO> chapitres = new ArrayList<>();

        public String getTitre() { return titre; }
        public void setTitre(String titre) { this.titre = titre; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        public Integer getOrdre() { return ordre; }
        public void setOrdre(Integer ordre) { this.ordre = ordre; }

        public List<ChapitreDTO> getChapitres() { return chapitres; }
        public void setChapitres(List<ChapitreDTO> chapitres) { this.chapitres = chapitres; }
    }

    public static class ChapitreDTO {
        private String titre;
        private String description;
        private Integer ordre;
        private List<ChapitreDTO> sousChapitres = new ArrayList<>();
        private List<ContenuItemDTO> contenus = new ArrayList<>();

        public String getTitre() { return titre; }
        public void setTitre(String titre) { this.titre = titre; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        public Integer getOrdre() { return ordre; }
        public void setOrdre(Integer ordre) { this.ordre = ordre; }

        public List<ChapitreDTO> getSousChapitres() { return sousChapitres; }
        public void setSousChapitres(List<ChapitreDTO> sousChapitres) { this.sousChapitres = sousChapitres; }

        public List<ContenuItemDTO> getContenus() { return contenus; }
        public void setContenus(List<ContenuItemDTO> contenus) { this.contenus = contenus; }
    }

    public static class ContenuItemDTO {
        private String titre;
        private String description;
        private String typeContenu;
        private Integer ordre;
        private Boolean apercuGratuit = false;
        private Boolean pleineLargeur = false;
        private String videoUrl;
        private Integer dureeVideo;
        private String contenuTexte;
        private String fichierUrl;
        private String imageLegende;
        private Integer imageLargeur;
        private Integer imageHauteur;
        private String lienExterne;
        private String lienTexte;

        public String getTitre() { return titre; }
        public void setTitre(String titre) { this.titre = titre; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        public String getTypeContenu() { return typeContenu; }
        public void setTypeContenu(String typeContenu) { this.typeContenu = typeContenu; }

        public Integer getOrdre() { return ordre; }
        public void setOrdre(Integer ordre) { this.ordre = ordre; }

        public Boolean getApercuGratuit() { return apercuGratuit; }
        public void setApercuGratuit(Boolean apercuGratuit) { this.apercuGratuit = apercuGratuit; }

        public Boolean getPleineLargeur() { return pleineLargeur; }
        public void setPleineLargeur(Boolean pleineLargeur) { this.pleineLargeur = pleineLargeur; }

        public String getVideoUrl() { return videoUrl; }
        public void setVideoUrl(String videoUrl) { this.videoUrl = videoUrl; }

        public Integer getDureeVideo() { return dureeVideo; }
        public void setDureeVideo(Integer dureeVideo) { this.dureeVideo = dureeVideo; }

        public String getContenuTexte() { return contenuTexte; }
        public void setContenuTexte(String contenuTexte) { this.contenuTexte = contenuTexte; }

        public String getFichierUrl() { return fichierUrl; }
        public void setFichierUrl(String fichierUrl) { this.fichierUrl = fichierUrl; }

        public String getImageLegende() { return imageLegende; }
        public void setImageLegende(String imageLegende) { this.imageLegende = imageLegende; }

        public Integer getImageLargeur() { return imageLargeur; }
        public void setImageLargeur(Integer imageLargeur) { this.imageLargeur = imageLargeur; }

        public Integer getImageHauteur() { return imageHauteur; }
        public void setImageHauteur(Integer imageHauteur) { this.imageHauteur = imageHauteur; }

        public String getLienExterne() { return lienExterne; }
        public void setLienExterne(String lienExterne) { this.lienExterne = lienExterne; }

        public String getLienTexte() { return lienTexte; }
        public void setLienTexte(String lienTexte) { this.lienTexte = lienTexte; }
    }
}