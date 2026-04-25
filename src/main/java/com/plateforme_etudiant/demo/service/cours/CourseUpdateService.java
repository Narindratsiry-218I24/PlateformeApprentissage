package com.plateforme_etudiant.demo.service.cours;

import com.plateforme_etudiant.demo.dto.CourseRequestDTO;
import com.plateforme_etudiant.demo.dto.CourseResponseDTO;
import com.plateforme_etudiant.demo.model.Categorie;
import com.plateforme_etudiant.demo.model.Chapitre;
import com.plateforme_etudiant.demo.model.ContenuItem;
import com.plateforme_etudiant.demo.model.Cours;
import com.plateforme_etudiant.demo.model.Section;
import com.plateforme_etudiant.demo.model.enums.TypeContenu;
import com.plateforme_etudiant.demo.repository.CategoryRepository;
import com.plateforme_etudiant.demo.repository.ChapitreRepository;
import com.plateforme_etudiant.demo.repository.ContenuItemRepository;
import com.plateforme_etudiant.demo.repository.CoursRepository;
import com.plateforme_etudiant.demo.repository.SectionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CourseUpdateService {

    private static final Logger log = LoggerFactory.getLogger(CourseUpdateService.class);

    private final CoursRepository coursRepository;
    private final CategoryRepository categoryRepository;
    private final SectionRepository sectionRepository;
    private final ChapitreRepository chapitreRepository;
    private final ContenuItemRepository contenuItemRepository;
    private final CourseConversionService conversionService;
    private final SlugGeneratorService slugGeneratorService;

    public CourseUpdateService(CoursRepository coursRepository,
                               CategoryRepository categoryRepository,
                               SectionRepository sectionRepository,
                               ChapitreRepository chapitreRepository,
                               ContenuItemRepository contenuItemRepository,
                               CourseConversionService conversionService,
                               SlugGeneratorService slugGeneratorService) {
        this.coursRepository = coursRepository;
        this.categoryRepository = categoryRepository;
        this.sectionRepository = sectionRepository;
        this.chapitreRepository = chapitreRepository;
        this.contenuItemRepository = contenuItemRepository;
        this.conversionService = conversionService;
        this.slugGeneratorService = slugGeneratorService;
    }

    @Transactional
    public CourseResponseDTO mettreAJourCours(Long coursId, CourseRequestDTO request) {
        log.info("Mise a jour du cours ID: {}", coursId);

        Cours cours = getCours(coursId);
        cours.setTitre(request.getTitre());
        cours.setSlug(slugGeneratorService.genererSlugCours(request.getTitre()));
        cours.setDescriptionCourte(request.getDescriptionCourte());
        cours.setDescription(request.getDescription());
        cours.setImageCouverture(request.getImageCouverture());
        cours.setDureeEstimee(request.getDureeEstimee());

        if (request.getCategorieId() != null) {
            Categorie categorie = categoryRepository.findById(request.getCategorieId())
                    .orElseThrow(() -> new RuntimeException("Categorie non trouvee avec l'ID: " + request.getCategorieId()));
            cours.setCategorie(categorie);
        }

        Cours coursMisAJour = coursRepository.save(cours);
        reconstruireStructureCours(coursMisAJour, request);
        log.info("Cours mis a jour avec succes, ID: {}", coursId);

        return conversionService.convertirEnReponse(coursMisAJour);
    }

    @Transactional
    public CourseResponseDTO publierCours(Long coursId) {
        log.info("Publication du cours ID: {}", coursId);

        Cours cours = getCours(coursId);
        cours.publier();
        Cours coursMisAJour = coursRepository.save(cours);

        log.info("Cours publie avec succes, ID: {}", coursId);
        return conversionService.convertirEnReponse(coursMisAJour);
    }

    @Transactional
    public CourseResponseDTO depublierCours(Long coursId) {
        log.info("Depublication du cours ID: {}", coursId);

        Cours cours = getCours(coursId);
        cours.depublier();
        Cours coursMisAJour = coursRepository.save(cours);

        log.info("Cours depublie avec succes, ID: {}", coursId);
        return conversionService.convertirEnReponse(coursMisAJour);
    }

    private Cours getCours(Long coursId) {
        return coursRepository.findById(coursId)
                .orElseThrow(() -> new RuntimeException("Cours non trouve avec l'ID: " + coursId));
    }

    private void reconstruireStructureCours(Cours cours, CourseRequestDTO request) {
        contenuItemRepository.deleteByCoursId(cours.getId());
        chapitreRepository.deleteByCoursId(cours.getId());
        sectionRepository.deleteByCoursId(cours.getId());

        if (request.getSections() == null || request.getSections().isEmpty()) {
            return;
        }

        for (int i = 0; i < request.getSections().size(); i++) {
            creerSection(cours, request.getSections().get(i), i);
        }
    }

    private void creerSection(Cours cours, CourseRequestDTO.SectionDTO sectionDTO, int ordre) {
        Section section = new Section();
        section.setCours(cours);
        section.setTitre(sectionDTO.getTitre());
        section.setDescription(sectionDTO.getDescription() != null ? sectionDTO.getDescription() : "");
        section.setOrdre(ordre);
        section = sectionRepository.save(section);

        if (sectionDTO.getChapitres() == null) {
            return;
        }

        for (int i = 0; i < sectionDTO.getChapitres().size(); i++) {
            creerChapitre(section, null, sectionDTO.getChapitres().get(i), i);
        }
    }

    private void creerChapitre(Section section, Chapitre parentChapitre, CourseRequestDTO.ChapitreDTO chapitreDTO, int ordre) {
        Chapitre chapitre = new Chapitre();
        chapitre.setSection(section);
        chapitre.setParentChapitre(parentChapitre);
        chapitre.setTitre(chapitreDTO.getTitre());
        chapitre.setDescription(chapitreDTO.getDescription() != null ? chapitreDTO.getDescription() : "");
        chapitre.setOrdre(ordre);
        chapitre = chapitreRepository.save(chapitre);

        if (chapitreDTO.getContenus() != null) {
            for (int i = 0; i < chapitreDTO.getContenus().size(); i++) {
                creerContenu(chapitre, chapitreDTO.getContenus().get(i), i);
            }
        }

        if (chapitreDTO.getSousChapitres() != null) {
            for (int i = 0; i < chapitreDTO.getSousChapitres().size(); i++) {
                creerChapitre(section, chapitre, chapitreDTO.getSousChapitres().get(i), i);
            }
        }
    }

    private void creerContenu(Chapitre chapitre, CourseRequestDTO.ContenuDTO contenuDTO, int ordre) {
        ContenuItem contenu = new ContenuItem();
        contenu.setChapitre(chapitre);
        contenu.setSection(chapitre.getSection());
        contenu.setTitre(contenuDTO.getTitre());
        contenu.setOrdre(ordre);
        contenu.setApercuGratuit(Boolean.TRUE.equals(contenuDTO.getApercuGratuit()));

        TypeContenu type = TypeContenu.valueOf(contenuDTO.getTypeContenu() != null ? contenuDTO.getTypeContenu() : "TEXTE");
        contenu.setTypeContenu(type);

        switch (type) {
            case VIDEO -> contenu.setVideoUrl(contenuDTO.getVideoUrl());
            case TEXTE -> contenu.setContenuTexte(contenuDTO.getContenuTexte());
            case LIEN -> {
                contenu.setLienExterne(contenuDTO.getLienExterne());
                contenu.setLienTexte(contenuDTO.getLienTexte());
            }
            case PDF, IMAGE, PRESENTATION -> contenu.setFichierUrl(contenuDTO.getFichierUrl());
            default -> {
            }
        }

        contenuItemRepository.save(contenu);
    }
}
