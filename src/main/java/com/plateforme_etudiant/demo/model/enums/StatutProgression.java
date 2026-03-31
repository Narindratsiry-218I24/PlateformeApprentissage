package com.plateforme_etudiant.demo.model.enums;

public enum StatutProgression {
    NON_COMMENCE("Non commencé"),
    EN_COURS("En cours"),
    TERMINE("Terminé");

    private final String libelle;

    StatutProgression(String libelle) {
        this.libelle = libelle;
    }

    public String getLibelle() {
        return libelle;
    }
}
