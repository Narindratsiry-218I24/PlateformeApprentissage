// CourseUpdateService.java
package com.plateforme_etudiant.demo.service.cours;

import com.plateforme_etudiant.demo.dto.CourseRequestDTO;
import com.plateforme_etudiant.demo.dto.CourseResponseDTO;
import com.plateforme_etudiant.demo.model.Categorie;
import com.plateforme_etudiant.demo.model.Cours;
import com.plateforme_etudiant.demo.repository.CategoryRepository;
import com.plateforme_etudiant.demo.repository.CoursRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CourseUpdateService {

    private static final Logger log = LoggerFactory.getLogger(CourseUpdateService.class);

    private final CoursRepository coursRepository;
    private final CategoryRepository categoryRepository;
    private final CourseConversionService conversionService;
    private final SlugGeneratorService slugGeneratorService;

    public CourseUpdateService(CoursRepository coursRepository,
                               CategoryRepository categoryRepository,
                               CourseConversionService conversionService,
                               SlugGeneratorService slugGeneratorService) {
        this.coursRepository = coursRepository;
        this.categoryRepository = categoryRepository;
        this.conversionService = conversionService;
        this.slugGeneratorService = slugGeneratorService;
    }

    @Transactional
    public CourseResponseDTO mettreAJourCours(Long coursId, CourseRequestDTO request) {
        log.info("Mise à jour du cours ID: {}", coursId);

        Cours cours = getCours(coursId);

        cours.setTitre(request.getTitre());
        cours.setSlug(slugGeneratorService.genererSlugCours(request.getTitre()));
        cours.setDescriptionCourte(request.getDescriptionCourte());
        cours.setDescription(request.getDescription());
        cours.setImageCouverture(request.getImageCouverture());
        cours.setDureeEstimee(request.getDureeEstimee());

        if (request.getCategorieId() != null) {
            Categorie categorie = categoryRepository.findById(request.getCategorieId())
                    .orElseThrow(() -> new RuntimeException("Catégorie non trouvée avec l'ID: " + request.getCategorieId()));
            cours.setCategorie(categorie);
        }

        Cours coursMisAJour = coursRepository.save(cours);
        log.info("Cours mis à jour avec succès, ID: {}", coursId);

        return conversionService.convertirEnReponse(coursMisAJour);
    }

    @Transactional
    public CourseResponseDTO publierCours(Long coursId) {
        log.info("Publication du cours ID: {}", coursId);

        Cours cours = getCours(coursId);
        cours.publier();
        Cours coursMisAJour = coursRepository.save(cours);

        log.info("Cours publié avec succès, ID: {}", coursId);
        return conversionService.convertirEnReponse(coursMisAJour);
    }

    @Transactional
    public CourseResponseDTO depublierCours(Long coursId) {
        log.info("Dépubliation du cours ID: {}", coursId);

        Cours cours = getCours(coursId);
        cours.depublier();
        Cours coursMisAJour = coursRepository.save(cours);

        log.info("Cours dépublie avec succès, ID: {}", coursId);
        return conversionService.convertirEnReponse(coursMisAJour);
    }

    private Cours getCours(Long coursId) {
        return coursRepository.findById(coursId)
                .orElseThrow(() -> new RuntimeException("Cours non trouvé avec l'ID: " + coursId));
    }
}