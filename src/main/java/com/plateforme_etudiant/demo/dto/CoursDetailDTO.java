// dto/CoursDetailDTO.java
package com.plateforme_etudiant.demo.dto;

import java.util.List;

public class CoursDetailDTO {
    private Long id;
    private String titre;
    private String slug;
    private String description;
    private String descriptionCourte;
    private String imageCouverture;
    private Integer progression;
    private List<SectionDetailDTO> sections;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }
    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getDescriptionCourte() { return descriptionCourte; }
    public void setDescriptionCourte(String descriptionCourte) { this.descriptionCourte = descriptionCourte; }
    public String getImageCouverture() { return imageCouverture; }
    public void setImageCouverture(String imageCouverture) { this.imageCouverture = imageCouverture; }
    public Integer getProgression() { return progression; }
    public void setProgression(Integer progression) { this.progression = progression; }
    public List<SectionDetailDTO> getSections() { return sections; }
    public void setSections(List<SectionDetailDTO> sections) { this.sections = sections; }

    public static class SectionDetailDTO {
        private Long id;
        private String titre;
        private String description;
        private Integer ordre;
        private List<ChapitreDetailDTO> chapitres;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getTitre() { return titre; }
        public void setTitre(String titre) { this.titre = titre; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public Integer getOrdre() { return ordre; }
        public void setOrdre(Integer ordre) { this.ordre = ordre; }
        public List<ChapitreDetailDTO> getChapitres() { return chapitres; }
        public void setChapitres(List<ChapitreDetailDTO> chapitres) { this.chapitres = chapitres; }
    }

    public static class ChapitreDetailDTO {
        private Long id;
        private String titre;
        private String description;
        private Integer ordre;
        private Integer niveauProfondeur;
        private List<ChapitreDetailDTO> sousChapitres;
        private List<ContenuDetailDTO> contenus;
        private Boolean estComplete;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getTitre() { return titre; }
        public void setTitre(String titre) { this.titre = titre; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public Integer getOrdre() { return ordre; }
        public void setOrdre(Integer ordre) { this.ordre = ordre; }
        public Integer getNiveauProfondeur() { return niveauProfondeur; }
        public void setNiveauProfondeur(Integer niveauProfondeur) { this.niveauProfondeur = niveauProfondeur; }
        public List<ChapitreDetailDTO> getSousChapitres() { return sousChapitres; }
        public void setSousChapitres(List<ChapitreDetailDTO> sousChapitres) { this.sousChapitres = sousChapitres; }
        public List<ContenuDetailDTO> getContenus() { return contenus; }
        public void setContenus(List<ContenuDetailDTO> contenus) { this.contenus = contenus; }
        public Boolean getEstComplete() { return estComplete; }
        public void setEstComplete(Boolean estComplete) { this.estComplete = estComplete; }
    }

    public static class ContenuDetailDTO {
        private Long id;
        private String titre;
        private String typeContenu;
        private String contenuPrincipal;
        private Integer ordre;
        private String videoUrl;
        private String contenuTexte;
        private String fichierUrl;
        private String lienExterne;
        private String lienTexte;
        private Boolean estComplete;
        private String htmlContent;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getTitre() { return titre; }
        public void setTitre(String titre) { this.titre = titre; }
        public String getTypeContenu() { return typeContenu; }
        public void setTypeContenu(String typeContenu) { this.typeContenu = typeContenu; }
        public String getContenuPrincipal() { return contenuPrincipal; }
        public void setContenuPrincipal(String contenuPrincipal) { this.contenuPrincipal = contenuPrincipal; }
        public Integer getOrdre() { return ordre; }
        public void setOrdre(Integer ordre) { this.ordre = ordre; }
        public String getVideoUrl() { return videoUrl; }
        public void setVideoUrl(String videoUrl) { this.videoUrl = videoUrl; }
        public String getContenuTexte() { return contenuTexte; }
        public void setContenuTexte(String contenuTexte) { this.contenuTexte = contenuTexte; }
        public String getFichierUrl() { return fichierUrl; }
        public void setFichierUrl(String fichierUrl) { this.fichierUrl = fichierUrl; }
        public String getLienExterne() { return lienExterne; }
        public void setLienExterne(String lienExterne) { this.lienExterne = lienExterne; }
        public String getLienTexte() { return lienTexte; }
        public void setLienTexte(String lienTexte) { this.lienTexte = lienTexte; }
        public Boolean getEstComplete() { return estComplete; }
        public void setEstComplete(Boolean estComplete) { this.estComplete = estComplete; }
        public String getHtmlContent() { return htmlContent; }
        public void setHtmlContent(String htmlContent) { this.htmlContent = htmlContent; }


    }
}