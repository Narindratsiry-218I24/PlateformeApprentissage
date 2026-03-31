package com.plateforme_etudiant.demo.model.enums;

public enum TypeRessource {
    PDF("PDF"),
    VIDEO("Vidéo"),
    LIEN("Lien externe"),
    IMAGE("Image"),
    DOCUMENT("Document");

    private final String libelle;

    TypeRessource(String libelle) {
        this.libelle = libelle;
    }

    public String getLibelle() {
        return libelle;
    }
}