package com.plateforme_etudiant.demo.controller.professeur;

import com.plateforme_etudiant.demo.dto.DashboardStatistiquesDTO;
import com.plateforme_etudiant.demo.dto.DernierCoursDTO;
import com.plateforme_etudiant.demo.dto.EtudiantEnLigneDTO;
import com.plateforme_etudiant.demo.model.Cours;
import com.plateforme_etudiant.demo.model.Progression;
import com.plateforme_etudiant.demo.model.Utilisateur;
import com.plateforme_etudiant.demo.repository.CoursRepository;
import com.plateforme_etudiant.demo.repository.InscriptionRepository;
import com.plateforme_etudiant.demo.repository.ProgressionRepository;
import com.plateforme_etudiant.demo.service.UtilisateurService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/professeur/api")
public class ProfesseurDashboardAPIController {

    @Autowired
    private UtilisateurService utilisateurService;
    
    @Autowired
    private CoursRepository coursRepository;
    
    @Autowired
    private InscriptionRepository inscriptionRepository;
    
    @Autowired
    private ProgressionRepository progressionRepository;

    @GetMapping("/etudiants-en-ligne")
    public ResponseEntity<List<EtudiantEnLigneDTO>> getEtudiantsEnLigne(HttpSession session) {
        Long professeurId = (Long) session.getAttribute("professeurId");
        if (professeurId == null) {
            return ResponseEntity.status(401).build();
        }

        // Active in last 5 minutes
        List<Utilisateur> apprenants = utilisateurService.getEtudiantsEnLigne(5);
        List<EtudiantEnLigneDTO> resultat = new ArrayList<>();

        for (Utilisateur u : apprenants) {
            EtudiantEnLigneDTO dto = new EtudiantEnLigneDTO();
            dto.setId(u.getId());
            dto.setNomComplet(u.getNomComplet());
            dto.setEmail(u.getEmail());

            if (u.getCoursActifId() != null) {
                Optional<Cours> coursOpt = coursRepository.findById(u.getCoursActifId());
                if (coursOpt.isPresent()) {
                    Cours cours = coursOpt.get();
                    // Optional: only show if course belongs to this professor
                    if (cours.getProfesseur() != null && cours.getProfesseur().getId().equals(professeurId)) {
                        dto.setCoursActuelId(cours.getId());
                        dto.setCoursActuelTitre(cours.getTitre());
                        
                        // Calcul basique pour l'UI
                        Integer progOpt = progressionRepository.getProgressionByCoursAndEtudiant(cours.getId(), u.getId());
                        dto.setProgression(progOpt != null ? progOpt.doubleValue() : 0.0);
                    } else {
                        // User is active but on another professor's course, or general page
                        dto.setCoursActuelTitre("Autre activité");
                        dto.setProgression(0.0);
                    }
                }
            } else {
                dto.setCoursActuelTitre("Dashboard/Menu");
                dto.setProgression(0.0);
            }

            if (u.getDerniereActivite() != null) {
                long minutes = Duration.between(u.getDerniereActivite(), LocalDateTime.now()).toMinutes();
                if (minutes == 0) {
                    dto.setStatutTemps("À l'instant");
                } else {
                    dto.setStatutTemps("Il y a " + minutes + " min");
                }
            }
            resultat.add(dto);
        }

        return ResponseEntity.ok(resultat);
    }

    @GetMapping("/derniers-cours")
    public ResponseEntity<List<DernierCoursDTO>> getDerniersCours(
            @RequestParam(defaultValue = "5") int limit, 
            HttpSession session) {
        
        Long professeurId = (Long) session.getAttribute("professeurId");
        if (professeurId == null) {
            return ResponseEntity.status(401).build();
        }

        List<Cours> cours = coursRepository.findByProfesseurIdOrderByDateCreationDesc(professeurId);
        cours.sort((c1, c2) -> {
            LocalDateTime d1 = c1.getDateModification() != null ? c1.getDateModification() : c1.getDateCreation();
            LocalDateTime d2 = c2.getDateModification() != null ? c2.getDateModification() : c2.getDateCreation();
            if (d1 == null && d2 == null) return 0;
            if (d1 == null) return 1;
            if (d2 == null) return -1;
            return d2.compareTo(d1);
        });

        List<DernierCoursDTO> dtos = cours.stream()
                .limit(limit)
                .map(c -> {
                    DernierCoursDTO dto = new DernierCoursDTO();
                    dto.setId(c.getId());
                    dto.setTitre(c.getTitre());
                    dto.setImageCouverture(c.getImageCouverture());
                    dto.setPublie(c.getPublie());
                    dto.setDateCreation(c.getDateCreation());
                    
                    int nbApprenants = inscriptionRepository.countByCoursId(c.getId());
                    dto.setNombreEtudiants(nbApprenants);
                    
                    Double prog = progressionRepository.getAverageProgressionByCoursId(c.getId());
                    dto.setProgressionMoyenne(prog != null ? prog : 0.0);
                    
                    return dto;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/statistiques")
    public ResponseEntity<DashboardStatistiquesDTO> getStatistiques(HttpSession session) {
        Long professeurId = (Long) session.getAttribute("professeurId");
        if (professeurId == null) {
            return ResponseEntity.status(401).build();
        }

        List<Cours> mesCours = coursRepository.findByProfesseurIdOrderByDateCreationDesc(professeurId);
        long totalCours = mesCours.size();
        long publies = mesCours.stream().filter(c -> Boolean.TRUE.equals(c.getPublie())).count();
        
        long totalEtudiants = mesCours.stream()
                .mapToLong(c -> inscriptionRepository.countByCoursId(c.getId()))
                .sum();
                
        double sumProg = 0;
        int countProg = 0;
        for (Cours c : mesCours) {
            Double p = progressionRepository.getAverageProgressionByCoursId(c.getId());
            if (p != null) {
                sumProg += p;
                countProg++;
            }
        }
        
        DashboardStatistiquesDTO dto = new DashboardStatistiquesDTO(
                totalCours, 
                publies, 
                totalEtudiants, 
                countProg > 0 ? (sumProg / countProg) : 0.0
        );

        return ResponseEntity.ok(dto);
    }
}
