package com.plateforme_etudiant.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DernierCoursDTO {
    private Long id;
    private String titre;
    private String imageCouverture;
    private Integer nombreEtudiants;
    private Double progressionMoyenne;
    private Boolean publie;
    private LocalDateTime dateCreation;
}
