package com.plateforme_etudiant.demo.model.enums;

public enum Role {
    ADMINISTRATEUR("Administrateur"),
    PROFESSEUR("Professeur"),
    APPRENANT("Apprenant");

    private final String libelle;

    Role(String libelle) {
        this.libelle = libelle;
    }

    public String getLibelle() {
        return libelle;
    }
}