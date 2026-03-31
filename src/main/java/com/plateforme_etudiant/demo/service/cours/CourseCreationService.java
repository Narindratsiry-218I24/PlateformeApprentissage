package com.plateforme_etudiant.demo.service.cours;

import com.plateforme_etudiant.demo.dto.CourseRequestDTO;
import com.plateforme_etudiant.demo.dto.CourseResponseDTO;
import com.plateforme_etudiant.demo.model.*;
import com.plateforme_etudiant.demo.model.enums.TypeContenu;
import com.plateforme_etudiant.demo.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CourseCreationService {

    private static final Logger log = LoggerFactory.getLogger(CourseCreationService.class);

    private final CoursRepository coursRepository;
    private final CategoryRepository categoryRepository;
    private final ProfesseurRepository professeurRepository;
    private final SectionRepository sectionRepository;
    private final ChapitreRepository chapitreRepository;
    private final ContenuItemRepository contenuItemRepository;
    private final CourseConversionService conversionService;
    private final SlugGeneratorService slugGeneratorService;

    public CourseCreationService(CoursRepository coursRepository,
                                 CategoryRepository categoryRepository,
                                 ProfesseurRepository professeurRepository,
                                 SectionRepository sectionRepository,
                                 ChapitreRepository chapitreRepository,
                                 ContenuItemRepository contenuItemRepository,
                                 CourseConversionService conversionService,
                                 SlugGeneratorService slugGeneratorService) {
        this.coursRepository = coursRepository;
        this.categoryRepository = categoryRepository;
        this.professeurRepository = professeurRepository;
        this.sectionRepository = sectionRepository;
        this.chapitreRepository = chapitreRepository;
        this.contenuItemRepository = contenuItemRepository;
        this.conversionService = conversionService;
        this.slugGeneratorService = slugGeneratorService;
    }

    @Transactional
    public CourseResponseDTO creerCours(CourseRequestDTO request, Long professeurId) {
        log.info("Création du cours: {}", request.getTitre());

        // 1. Récupérer le professeur
        Professeur professeur = professeurRepository.findById(professeurId)
                .orElseThrow(() -> new RuntimeException("Professeur non trouvé"));

        // 2. Gérer la catégorie (optionnelle)
        Categorie categorie = null;
        if (request.getCategorieId() != null) {
            // Si un ID de catégorie est fourni, on le cherche
            categorie = categoryRepository.findById(request.getCategorieId()).orElse(null);
            if (categorie != null) {
                log.info("Catégorie trouvée: {}", categorie.getNom());
            } else {
                log.warn("Catégorie avec ID {} non trouvée, cours sans catégorie", request.getCategorieId());
            }
        } else {
            log.info("Aucune catégorie spécifiée, cours créé sans catégorie");
        }

        // 3. Créer le cours
        Cours cours = new Cours();
        cours.setTitre(request.getTitre());
        cours.setSlug(slugGeneratorService.genererSlugCours(request.getTitre()));
        cours.setDescriptionCourte(request.getDescriptionCourte());
        cours.setDescription(request.getDescription());
        cours.setImageCouverture(request.getImageCouverture());
        cours.setDureeEstimee(request.getDureeEstimee());
        cours.setCategorie(categorie);  // Peut être null
        cours.setProfesseur(professeur);
        cours.setPublie(false);
        cours.setNombreApprenants(0);

        Cours coursSauvegarde = coursRepository.save(cours);
        log.info("Cours créé avec ID: {}", coursSauvegarde.getId());

        // 4. Créer les sections
        if (request.getSections() != null) {
            for (int i = 0; i < request.getSections().size(); i++) {
                creerSection(request.getSections().get(i), coursSauvegarde, i);
            }
        }

        return conversionService.convertirEnReponse(coursSauvegarde);
    }

    private void creerSection(CourseRequestDTO.SectionDTO sectionDTO, Cours cours, int ordre) {
        Section section = new Section();
        section.setTitre(sectionDTO.getTitre());
        section.setDescription(sectionDTO.getDescription());
        section.setOrdre(sectionDTO.getOrdre() != null ? sectionDTO.getOrdre() : ordre);
        section.setCours(cours);

        Section sectionSauvegarde = sectionRepository.save(section);
        log.debug("Section créée: {}", sectionSauvegarde.getTitre());

        if (sectionDTO.getChapitres() != null) {
            for (int i = 0; i < sectionDTO.getChapitres().size(); i++) {
                creerChapitreRecursif(sectionDTO.getChapitres().get(i), sectionSauvegarde, null, i);
            }
        }
    }

    private void creerChapitreRecursif(CourseRequestDTO.ChapitreDTO chapitreDTO,
                                       Section section,
                                       Chapitre parent,
                                       int ordre) {
        Chapitre chapitre = new Chapitre();
        chapitre.setTitre(chapitreDTO.getTitre());
        chapitre.setDescription(chapitreDTO.getDescription());
        chapitre.setOrdre(chapitreDTO.getOrdre() != null ? chapitreDTO.getOrdre() : ordre);
        chapitre.setSection(section);
        chapitre.setParentChapitre(parent);

        Chapitre chapitreSauvegarde = chapitreRepository.save(chapitre);
        log.debug("Chapitre créé: {}", chapitreSauvegarde.getTitre());

        if (chapitreDTO.getContenus() != null) {
            for (int i = 0; i < chapitreDTO.getContenus().size(); i++) {
                creerContenuItem(chapitreDTO.getContenus().get(i), chapitreSauvegarde, section, i);
            }
        }

        if (chapitreDTO.getSousChapitres() != null) {
            for (int i = 0; i < chapitreDTO.getSousChapitres().size(); i++) {
                creerChapitreRecursif(chapitreDTO.getSousChapitres().get(i), section, chapitreSauvegarde, i);
            }
        }
    }

    private void creerContenuItem(CourseRequestDTO.ContenuItemDTO dto,
                                  Chapitre chapitre,
                                  Section section,
                                  int ordre) {
        ContenuItem contenu = new ContenuItem();
        contenu.setTitre(dto.getTitre());
        contenu.setDescription(dto.getDescription());
        contenu.setChapitre(chapitre);
        contenu.setSection(section);
        contenu.setOrdre(dto.getOrdre() != null ? dto.getOrdre() : ordre);
        contenu.setApercuGratuit(dto.getApercuGratuit() != null ? dto.getApercuGratuit() : false);
        contenu.setPleineLargeur(dto.getPleineLargeur() != null ? dto.getPleineLargeur() : false);

        TypeContenu type = TypeContenu.valueOf(dto.getTypeContenu().toUpperCase());
        contenu.setTypeContenu(type);

        switch (type) {
            case TEXTE:
                contenu.setContenuTexte(dto.getContenuTexte());
                break;
            case VIDEO:
                contenu.setVideoUrl(dto.getVideoUrl());
                contenu.setDureeVideo(dto.getDureeVideo());
                break;
            case PDF:
            case PRESENTATION:
                contenu.setFichierUrl(dto.getFichierUrl());
                break;
            case IMAGE:
                contenu.setFichierUrl(dto.getFichierUrl());
                contenu.setImageLegende(dto.getImageLegende());
                contenu.setImageLargeur(dto.getImageLargeur());
                contenu.setImageHauteur(dto.getImageHauteur());
                break;
            case LIEN:
                contenu.setLienExterne(dto.getLienExterne());
                contenu.setLienTexte(dto.getLienTexte());
                break;
        }

        contenuItemRepository.save(contenu);
    }
}