package com.plateforme_etudiant.demo.dto;

import java.util.List;

public class CourseRequestDTO {
    private String titre;
    private String descriptionCourte;
    private String description;
    private Integer dureeEstimee;
    private String imageCouverture;
    private boolean publie;
    private Long categorieId;
    private List<SectionDTO> sections;

    // Getters et Setters
    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }

    public String getDescriptionCourte() { return descriptionCourte; }
    public void setDescriptionCourte(String descriptionCourte) { this.descriptionCourte = descriptionCourte; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getDureeEstimee() { return dureeEstimee; }
    public void setDureeEstimee(Integer dureeEstimee) { this.dureeEstimee = dureeEstimee; }

    public String getImageCouverture() { return imageCouverture; }
    public void setImageCouverture(String imageCouverture) { this.imageCouverture = imageCouverture; }

    public boolean isPublie() { return publie; }
    public void setPublie(boolean publie) { this.publie = publie; }

    public Long getCategorieId() { return categorieId; }
    public void setCategorieId(Long categorieId) { this.categorieId = categorieId; }

    public List<SectionDTO> getSections() { return sections; }
    public void setSections(List<SectionDTO> sections) { this.sections = sections; }

    // ========== CLASSES INTERNES ==========

    public static class SectionDTO {
        private String titre;
        private String description;
        private Integer ordre;
        private List<ChapitreDTO> chapitres;

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
        private List<ContenuDTO> contenus;
        private List<ChapitreDTO> sousChapitres;

        public String getTitre() { return titre; }
        public void setTitre(String titre) { this.titre = titre; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public Integer getOrdre() { return ordre; }
        public void setOrdre(Integer ordre) { this.ordre = ordre; }
        public List<ContenuDTO> getContenus() { return contenus; }
        public void setContenus(List<ContenuDTO> contenus) { this.contenus = contenus; }
        public List<ChapitreDTO> getSousChapitres() { return sousChapitres; }
        public void setSousChapitres(List<ChapitreDTO> sousChapitres) { this.sousChapitres = sousChapitres; }
    }

    public static class ContenuDTO {
        private String titre;
        private String typeContenu;
        private Boolean apercuGratuit;
        private Integer ordre;
        private String videoUrl;
        private String contenuTexte;
        private String lienExterne;
        private String lienTexte;
        private String fichierUrl;  // ← AJOUTÉ

        // Getters
        public String getTitre() { return titre; }
        public String getTypeContenu() { return typeContenu; }
        public Boolean getApercuGratuit() { return apercuGratuit; }
        public Integer getOrdre() { return ordre; }
        public String getVideoUrl() { return videoUrl; }
        public String getContenuTexte() { return contenuTexte; }
        public String getLienExterne() { return lienExterne; }
        public String getLienTexte() { return lienTexte; }
        public String getFichierUrl() { return fichierUrl; }

        // Setters
        public void setTitre(String titre) { this.titre = titre; }
        public void setTypeContenu(String typeContenu) { this.typeContenu = typeContenu; }
        public void setApercuGratuit(Boolean apercuGratuit) { this.apercuGratuit = apercuGratuit; }
        public void setOrdre(Integer ordre) { this.ordre = ordre; }
        public void setVideoUrl(String videoUrl) { this.videoUrl = videoUrl; }
        public void setContenuTexte(String contenuTexte) { this.contenuTexte = contenuTexte; }
        public void setLienExterne(String lienExterne) { this.lienExterne = lienExterne; }
        public void setLienTexte(String lienTexte) { this.lienTexte = lienTexte; }
        public void setFichierUrl(String fichierUrl) { this.fichierUrl = fichierUrl; }
    }
}