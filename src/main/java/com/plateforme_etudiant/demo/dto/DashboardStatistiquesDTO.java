package com.plateforme_etudiant.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatistiquesDTO {
    private Long totalCours;
    private Long coursPublies;
    private Long totalEtudiants;
    private Double progressionMoyenne;
}
