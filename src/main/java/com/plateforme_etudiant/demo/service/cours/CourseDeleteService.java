// CourseDeleteService.java
package com.plateforme_etudiant.demo.service.cours;

import com.plateforme_etudiant.demo.model.Chapitre;
import com.plateforme_etudiant.demo.model.ContenuItem;
import com.plateforme_etudiant.demo.model.Cours;
import com.plateforme_etudiant.demo.model.Section;
import com.plateforme_etudiant.demo.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CourseDeleteService {

    private static final Logger log = LoggerFactory.getLogger(CourseDeleteService.class);

    private final CoursRepository coursRepository;
    private final SectionRepository sectionRepository;
    private final ChapitreRepository chapitreRepository;
    private final ContenuItemRepository contenuItemRepository;
    private final FavoriRepository favoriRepository;
    private final ProgressionRepository progressionRepository;
    private final RessourceRepository ressourceRepository;

    public CourseDeleteService(CoursRepository coursRepository,
                               SectionRepository sectionRepository,
                               ChapitreRepository chapitreRepository,
                               ContenuItemRepository contenuItemRepository,
                               FavoriRepository favoriRepository,
                               ProgressionRepository progressionRepository,
                               RessourceRepository ressourceRepository) {
        this.coursRepository = coursRepository;
        this.sectionRepository = sectionRepository;
        this.chapitreRepository = chapitreRepository;
        this.contenuItemRepository = contenuItemRepository;
        this.favoriRepository = favoriRepository;
        this.progressionRepository = progressionRepository;
        this.ressourceRepository = ressourceRepository;
    }

    @Transactional
    public void supprimerCours(Long coursId) {
        Cours cours = coursRepository.findById(coursId)
                .orElseThrow(() -> new RuntimeException("Cours non trouvé avec l'ID: " + coursId));

        log.info("Suppression du cours: {} (ID: {})", cours.getTitre(), coursId);

        try {
            favoriRepository.deleteByCoursId(coursId);
            progressionRepository.deleteByCoursId(coursId);
            ressourceRepository.deleteByCoursId(coursId);
            contenuItemRepository.deleteByCoursId(coursId);
            chapitreRepository.deleteByCoursId(coursId);
            sectionRepository.deleteByCoursId(coursId);
            coursRepository.delete(cours);

            log.info("Cours supprimé avec succès, ID: {}", coursId);
        } catch (Exception e) {
            log.error("Erreur lors de la suppression du cours ID: {}", coursId, e);
            throw new RuntimeException("Erreur lors de la suppression du cours: " + e.getMessage(), e);
        }
    }

    @Transactional
    public void supprimerSection(Long sectionId) {
        Section section = sectionRepository.findById(sectionId)
                .orElseThrow(() -> new RuntimeException("Section non trouvée avec l'ID: " + sectionId));

        log.info("Suppression de la section: {} (ID: {})", section.getTitre(), sectionId);

        try {
            List<Chapitre> chapitres = chapitreRepository.findBySectionIdOrderByOrdre(sectionId);
            for (Chapitre chapitre : chapitres) {
                supprimerChapitreRecursif(chapitre.getId());
            }
            sectionRepository.delete(section);
            log.info("Section supprimée avec succès, ID: {}", sectionId);
        } catch (Exception e) {
            log.error("Erreur lors de la suppression de la section ID: {}", sectionId, e);
            throw new RuntimeException("Erreur lors de la suppression de la section: " + e.getMessage(), e);
        }
    }

    @Transactional
    public void supprimerChapitre(Long chapitreId) {
        supprimerChapitreRecursif(chapitreId);
    }

    private void supprimerChapitreRecursif(Long chapitreId) {
        Chapitre chapitre = chapitreRepository.findById(chapitreId)
                .orElseThrow(() -> new RuntimeException("Chapitre non trouvé avec l'ID: " + chapitreId));

        log.info("Suppression du chapitre: {} (ID: {})", chapitre.getTitre(), chapitreId);

        try {
            List<Chapitre> sousChapitres = chapitreRepository.findByParentChapitreIdOrderByOrdre(chapitreId);
            for (Chapitre sousChapitre : sousChapitres) {
                supprimerChapitreRecursif(sousChapitre.getId());
            }

            List<ContenuItem> contenus = contenuItemRepository.findByChapitreIdOrderByOrdre(chapitreId);
            for (ContenuItem contenu : contenus) {
                supprimerContenuItem(contenu.getId());
            }

            chapitreRepository.delete(chapitre);
            log.info("Chapitre supprimé avec succès, ID: {}", chapitreId);
        } catch (Exception e) {
            log.error("Erreur lors de la suppression du chapitre ID: {}", chapitreId, e);
            throw new RuntimeException("Erreur lors de la suppression du chapitre: " + e.getMessage(), e);
        }
    }

    @Transactional
    public void supprimerContenuItem(Long contenuId) {
        ContenuItem contenu = contenuItemRepository.findById(contenuId)
                .orElseThrow(() -> new RuntimeException("Contenu non trouvé avec l'ID: " + contenuId));

        log.info("Suppression du contenu: {} (ID: {})", contenu.getTitre(), contenuId);

        try {
            favoriRepository.deleteByContenuItemId(contenuId);
            progressionRepository.deleteByContenuItemId(contenuId);
            ressourceRepository.deleteByContenuItemId(contenuId);
            contenuItemRepository.delete(contenu);

            log.info("Contenu supprimé avec succès, ID: {}", contenuId);
        } catch (Exception e) {
            log.error("Erreur lors de la suppression du contenu ID: {}", contenuId, e);
            throw new RuntimeException("Erreur lors de la suppression du contenu: " + e.getMessage(), e);
        }
    }
}