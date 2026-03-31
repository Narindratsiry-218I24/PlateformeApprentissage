// CourseResponseDTO.java
package com.plateforme_etudiant.demo.dto;

import java.time.LocalDateTime;
import java.util.List;

public class CourseResponseDTO {

    private Long id;
    private String titre;
    private String slug;
    private String description;
    private String descriptionCourte;
    private String imageCouverture;
    private Boolean publie;
    private Integer dureeEstimee;
    private Integer nombreApprenants;
    private LocalDateTime dateCreation;
    private LocalDateTime datePublication;
    private CategorieInfo categorie;
    private ProfesseurInfo professeur;
    private List<SectionInfo> sections;

    // ========== GETTERS ==========
    public Long getId() {
        return id;
    }

    public String getTitre() {
        return titre;
    }

    public String getSlug() {
        return slug;
    }

    public String getDescription() {
        return description;
    }

    public String getDescriptionCourte() {
        return descriptionCourte;
    }

    public String getImageCouverture() {
        return imageCouverture;
    }

    public Boolean getPublie() {
        return publie;
    }

    public Integer getDureeEstimee() {
        return dureeEstimee;
    }

    public Integer getNombreApprenants() {
        return nombreApprenants;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public LocalDateTime getDatePublication() {
        return datePublication;
    }

    public CategorieInfo getCategorie() {
        return categorie;
    }

    public ProfesseurInfo getProfesseur() {
        return professeur;
    }

    public List<SectionInfo> getSections() {
        return sections;
    }

    // ========== SETTERS ==========
    public void setId(Long id) {
        this.id = id;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDescriptionCourte(String descriptionCourte) {
        this.descriptionCourte = descriptionCourte;
    }

    public void setImageCouverture(String imageCouverture) {
        this.imageCouverture = imageCouverture;
    }

    public void setPublie(Boolean publie) {
        this.publie = publie;
    }

    public void setDureeEstimee(Integer dureeEstimee) {
        this.dureeEstimee = dureeEstimee;
    }

    public void setNombreApprenants(Integer nombreApprenants) {
        this.nombreApprenants = nombreApprenants;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }

    public void setDatePublication(LocalDateTime datePublication) {
        this.datePublication = datePublication;
    }

    public void setCategorie(CategorieInfo categorie) {
        this.categorie = categorie;
    }

    public void setProfesseur(ProfesseurInfo professeur) {
        this.professeur = professeur;
    }

    public void setSections(List<SectionInfo> sections) {
        this.sections = sections;
    }

    // ========== CLASSES INTERNES ==========

    public static class CategorieInfo {
        private Long id;
        private String nom;
        private String slug;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public String getNom() { return nom; }
        public void setNom(String nom) { this.nom = nom; }

        public String getSlug() { return slug; }
        public void setSlug(String slug) { this.slug = slug; }
    }

    public static class ProfesseurInfo {
        private Long id;
        private String specialite;
        private String biographie;
        private String nomComplet;
        private String email;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public String getSpecialite() { return specialite; }
        public void setSpecialite(String specialite) { this.specialite = specialite; }

        public String getBiographie() { return biographie; }
        public void setBiographie(String biographie) { this.biographie = biographie; }

        public String getNomComplet() { return nomComplet; }
        public void setNomComplet(String nomComplet) { this.nomComplet = nomComplet; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
    }

    public static class SectionInfo {
        private Long id;
        private String titre;
        private String description;
        private Integer ordre;
        private Integer nombreTotalContenus;
        private List<ChapitreInfo> chapitres;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public String getTitre() { return titre; }
        public void setTitre(String titre) { this.titre = titre; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        public Integer getOrdre() { return ordre; }
        public void setOrdre(Integer ordre) { this.ordre = ordre; }

        public Integer getNombreTotalContenus() { return nombreTotalContenus; }
        public void setNombreTotalContenus(Integer nombreTotalContenus) { this.nombreTotalContenus = nombreTotalContenus; }

        public List<ChapitreInfo> getChapitres() { return chapitres; }
        public void setChapitres(List<ChapitreInfo> chapitres) { this.chapitres = chapitres; }
    }

    public static class ChapitreInfo {
        private Long id;
        private String titre;
        private String description;
        private Integer ordre;
        private Integer niveauProfondeur;
        private Boolean aDesSousChapitres;
        private Integer nombreTotalContenus;
        private List<ChapitreInfo> sousChapitres;
        private List<ContenuItemInfo> contenus;

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

        public Boolean getADesSousChapitres() { return aDesSousChapitres; }
        public void setADesSousChapitres(Boolean aDesSousChapitres) { this.aDesSousChapitres = aDesSousChapitres; }

        public Integer getNombreTotalContenus() { return nombreTotalContenus; }
        public void setNombreTotalContenus(Integer nombreTotalContenus) { this.nombreTotalContenus = nombreTotalContenus; }

        public List<ChapitreInfo> getSousChapitres() { return sousChapitres; }
        public void setSousChapitres(List<ChapitreInfo> sousChapitres) { this.sousChapitres = sousChapitres; }

        public List<ContenuItemInfo> getContenus() { return contenus; }
        public void setContenus(List<ContenuItemInfo> contenus) { this.contenus = contenus; }
    }

    public static class ContenuItemInfo {
        private Long id;
        private String titre;
        private String description;
        private String typeContenu;
        private String contenuPrincipal;
        private Integer ordre;
        private Boolean apercuGratuit;
        private Boolean pleineLargeur;
        private String videoUrl;
        private Integer dureeVideo;
        private String contenuTexte;
        private String fichierUrl;
        private String imageLegende;
        private Integer imageLargeur;
        private Integer imageHauteur;
        private String lienExterne;
        private String lienTexte;
        private String htmlContent;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public String getTitre() { return titre; }
        public void setTitre(String titre) { this.titre = titre; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        public String getTypeContenu() { return typeContenu; }
        public void setTypeContenu(String typeContenu) { this.typeContenu = typeContenu; }

        public String getContenuPrincipal() { return contenuPrincipal; }
        public void setContenuPrincipal(String contenuPrincipal) { this.contenuPrincipal = contenuPrincipal; }

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

        public String getHtmlContent() { return htmlContent; }
        public void setHtmlContent(String htmlContent) { this.htmlContent = htmlContent; }
    }
}