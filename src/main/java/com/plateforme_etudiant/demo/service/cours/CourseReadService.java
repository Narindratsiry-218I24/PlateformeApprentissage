// CourseReadService.java
package com.plateforme_etudiant.demo.service.cours;

import com.plateforme_etudiant.demo.dto.CourseResponseDTO;
import com.plateforme_etudiant.demo.model.Cours;
import com.plateforme_etudiant.demo.model.Professeur;
import com.plateforme_etudiant.demo.repository.CoursRepository;
import com.plateforme_etudiant.demo.repository.ProfesseurRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CourseReadService {

    private static final Logger log = LoggerFactory.getLogger(CourseReadService.class);

    private final CoursRepository coursRepository;
    private final ProfesseurRepository professeurRepository;
    private final CourseConversionService conversionService;

    public CourseReadService(CoursRepository coursRepository,
                             ProfesseurRepository professeurRepository,
                             CourseConversionService conversionService) {
        this.coursRepository = coursRepository;
        this.professeurRepository = professeurRepository;
        this.conversionService = conversionService;
    }

    @Transactional(readOnly = true)
    public List<CourseResponseDTO> getCoursParProfesseur(Long professeurId) {
        log.debug("Récupération des cours pour le professeur ID: {}", professeurId);

        Professeur professeur = professeurRepository.findById(professeurId)
                .orElseThrow(() -> new RuntimeException("Professeur non trouvé avec l'ID: " + professeurId));

        return coursRepository.findByProfesseur(professeur)
                .stream()
                .map(conversionService::convertirEnReponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CourseResponseDTO getCoursParId(Long coursId) {
        log.debug("Récupération du cours par ID: {}", coursId);

        Cours cours = coursRepository.findById(coursId)
                .orElseThrow(() -> new RuntimeException("Cours non trouvé avec l'ID: " + coursId));
        return conversionService.convertirEnReponse(cours);
    }

    @Transactional(readOnly = true)
    public CourseResponseDTO getCoursParIdPourProfesseur(Long coursId, Long professeurId) {
        log.debug("Recuperation du cours {} pour le professeur {}", coursId, professeurId);

        Cours cours = coursRepository.findByIdAndProfesseurId(coursId, professeurId)
                .orElseThrow(() -> new RuntimeException("Cours introuvable pour ce professeur"));
        return conversionService.convertirEnReponse(cours);
    }

    @Transactional(readOnly = true)
    public List<CourseResponseDTO> getCoursPublies() {
        log.debug("Récupération de tous les cours publiés");

        return coursRepository.findByPublieTrueOrderByDateCreationDesc()
                .stream()
                .map(conversionService::convertirEnReponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CourseResponseDTO> searchCours(String keyword) {
        log.debug("Recherche de cours avec le mot-clé: {}", keyword);

        return coursRepository.searchByKeyword(keyword)
                .stream()
                .map(conversionService::convertirEnReponse)
                .collect(Collectors.toList());
    }
}
