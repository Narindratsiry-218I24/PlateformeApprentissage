package com.plateforme_etudiant.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EtudiantEnLigneDTO {
    private Long id;
    private String nomComplet;
    private String email;
    private Long coursActuelId;
    private String coursActuelTitre;
    private Double progression;
    private String statutTemps;
}
