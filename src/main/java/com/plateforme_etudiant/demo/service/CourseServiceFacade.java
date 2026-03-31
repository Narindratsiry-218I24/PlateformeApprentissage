// CourseServiceFacade.java
package com.plateforme_etudiant.demo.service;

import com.plateforme_etudiant.demo.dto.CourseRequestDTO;
import com.plateforme_etudiant.demo.dto.CourseResponseDTO;
import com.plateforme_etudiant.demo.service.cours.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseServiceFacade {

    private final CourseCreationService creationService;
    private final CourseReadService readService;
    private final CourseUpdateService updateService;
    private final CourseDeleteService deleteService;

    // Constructeur explicite
    public CourseServiceFacade(CourseCreationService creationService,
                               CourseReadService readService,
                               CourseUpdateService updateService,
                               CourseDeleteService deleteService) {
        this.creationService = creationService;
        this.readService = readService;
        this.updateService = updateService;
        this.deleteService = deleteService;
    }

    // ==================== CRÉATION ====================

    public CourseResponseDTO creerCours(CourseRequestDTO request, Long professeurId) {
        return creationService.creerCours(request, professeurId);
    }

    // ==================== LECTURE ====================

    public List<CourseResponseDTO> getCoursParProfesseur(Long professeurId) {
        return readService.getCoursParProfesseur(professeurId);
    }

    public CourseResponseDTO getCoursParId(Long coursId) {
        return readService.getCoursParId(coursId);
    }

    public List<CourseResponseDTO> getCoursPublies() {
        return readService.getCoursPublies();
    }

    public List<CourseResponseDTO> searchCours(String keyword) {
        return readService.searchCours(keyword);
    }

    // ==================== MISE À JOUR ====================

    public CourseResponseDTO mettreAJourCours(Long coursId, CourseRequestDTO request) {
        return updateService.mettreAJourCours(coursId, request);
    }

    public CourseResponseDTO publierCours(Long coursId) {
        return updateService.publierCours(coursId);
    }

    public CourseResponseDTO depublierCours(Long coursId) {
        return updateService.depublierCours(coursId);
    }

    // ==================== SUPPRESSION ====================

    public void supprimerCours(Long coursId) {
        deleteService.supprimerCours(coursId);
    }

    public void supprimerSection(Long sectionId) {
        deleteService.supprimerSection(sectionId);
    }

    public void supprimerChapitre(Long chapitreId) {
        deleteService.supprimerChapitre(chapitreId);
    }

    public void supprimerContenuItem(Long contenuId) {
        deleteService.supprimerContenuItem(contenuId);
    }
}