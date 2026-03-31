// TypeContenu.java
package com.plateforme_etudiant.demo.model.enums;

public enum TypeContenu {
    VIDEO("Vidéo", "Contenu vidéo avec URL et durée"),
    TEXTE("Texte", "Contenu textuel formaté"),
    PDF("Document PDF", "Fichier PDF à télécharger"),
    PRESENTATION("Présentation", "Présentation PowerPoint/Google Slides"),
    LIEN("Lien externe", "Lien vers une ressource externe"),
    IMAGE("Image", "Image illustrative (schéma, diagramme, illustration)");

    private final String libelle;
    private final String description;

    TypeContenu(String libelle, String description) {
        this.libelle = libelle;
        this.description = description;
    }

    public String getLibelle() {
        return libelle;
    }

    public String getDescription() {
        return description;
    }
}