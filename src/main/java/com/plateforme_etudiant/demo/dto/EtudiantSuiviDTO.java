package com.plateforme_etudiant.demo.dto;

import java.time.LocalDateTime;

public class EtudiantSuiviDTO {
    private Long id;
    private String nomComplet;
    private String email;
    private String nomUtilisateur;
    private Integer nombreCours;
    private Integer progressionMoyenne;
    private LocalDateTime derniereActivite;

    public EtudiantSuiviDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNomComplet() { return nomComplet; }
    public void setNomComplet(String nomComplet) { this.nomComplet = nomComplet; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getNomUtilisateur() { return nomUtilisateur; }
    public void setNomUtilisateur(String nomUtilisateur) { this.nomUtilisateur = nomUtilisateur; }
    public Integer getNombreCours() { return nombreCours; }
    public void setNombreCours(Integer nombreCours) { this.nombreCours = nombreCours; }
    public Integer getProgressionMoyenne() { return progressionMoyenne; }
    public void setProgressionMoyenne(Integer progressionMoyenne) { this.progressionMoyenne = progressionMoyenne; }
    public LocalDateTime getDerniereActivite() { return derniereActivite; }
    public void setDerniereActivite(LocalDateTime derniereActivite) { this.derniereActivite = derniereActivite; }
}