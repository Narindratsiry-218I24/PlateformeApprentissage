// ContenuItem.java - Vérifier que l'enum est correct
package com.plateforme_etudiant.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.plateforme_etudiant.demo.model.enums.TypeContenu;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

@Entity
@Table(name = "contenu_items")
public class ContenuItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chapitre_id", nullable = false)
    @JsonIgnore
    private Chapitre chapitre;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "section_id", nullable = false)
    @JsonIgnore
    private Section section;

    @NotBlank(message = "Le titre du contenu est obligatoire")
    @Size(max = 200, message = "Le titre ne peut pas dépasser 200 caractères")
    @Column(length = 200, nullable = false)
    private String titre;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_contenu", length = 20, nullable = false)
    private TypeContenu typeContenu;

    @Column(nullable = false)
    private Integer ordre = 0;

    @Column(name = "est_apercu_gratuit", nullable = false)
    private Boolean apercuGratuit = false;

    @Column(name = "pleine_largeur")
    private Boolean pleineLargeur = false;

    // Pour TEXTE
    @Column(columnDefinition = "TEXT")
    private String contenuTexte;

    // Pour VIDEO
    @Column(name = "video_url", length = 500)
    private String videoUrl;

    @Column(name = "duree_video")
    private Integer dureeVideo;

    // Pour PDF, PRESENTATION, IMAGE
    @Column(name = "fichier_url", length = 500)
    private String fichierUrl;

    // Pour IMAGE
    @Column(name = "image_legende", length = 255)
    private String imageLegende;

    @Column(name = "image_largeur")
    private Integer imageLargeur;

    @Column(name = "image_hauteur")
    private Integer imageHauteur;

    // Pour LIEN
    @Column(name = "lien_externe", length = 500)
    private String lienExterne;

    @Column(name = "lien_texte", length = 200)
    private String lienTexte;

    @CreationTimestamp
    @Column(name = "date_creation", updatable = false)
    private LocalDateTime dateCreation;

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Chapitre getChapitre() { return chapitre; }
    public void setChapitre(Chapitre chapitre) { this.chapitre = chapitre; }

    public Section getSection() { return section; }
    public void setSection(Section section) { this.section = section; }

    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public TypeContenu getTypeContenu() { return typeContenu; }
    public void setTypeContenu(TypeContenu typeContenu) { this.typeContenu = typeContenu; }

    public Integer getOrdre() { return ordre; }
    public void setOrdre(Integer ordre) { this.ordre = ordre; }

    public Boolean getApercuGratuit() { return apercuGratuit; }
    public void setApercuGratuit(Boolean apercuGratuit) { this.apercuGratuit = apercuGratuit; }

    public Boolean getPleineLargeur() { return pleineLargeur; }
    public void setPleineLargeur(Boolean pleineLargeur) { this.pleineLargeur = pleineLargeur; }

    public String getContenuTexte() { return contenuTexte; }
    public void setContenuTexte(String contenuTexte) { this.contenuTexte = contenuTexte; }

    public String getVideoUrl() { return videoUrl; }
    public void setVideoUrl(String videoUrl) { this.videoUrl = videoUrl; }

    public Integer getDureeVideo() { return dureeVideo; }
    public void setDureeVideo(Integer dureeVideo) { this.dureeVideo = dureeVideo; }

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

    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }

    // Méthodes utilitaires
    public String getContenuPrincipal() {
        switch (typeContenu) {
            case TEXTE: return contenuTexte;
            case VIDEO: return videoUrl;
            case PDF:
            case PRESENTATION:
            case IMAGE: return fichierUrl;
            case LIEN: return lienExterne;
            default: return null;
        }
    }

    public boolean estVideo() { return TypeContenu.VIDEO.equals(typeContenu); }
    public boolean estTexte() { return TypeContenu.TEXTE.equals(typeContenu); }
    public boolean estPdf() { return TypeContenu.PDF.equals(typeContenu); }
    public boolean estPresentation() { return TypeContenu.PRESENTATION.equals(typeContenu); }
    public boolean estLien() { return TypeContenu.LIEN.equals(typeContenu); }
    public boolean estImage() { return TypeContenu.IMAGE.equals(typeContenu); }
    public boolean estGratuit() { return apercuGratuit != null && apercuGratuit; }
}