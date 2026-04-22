package com.plateforme_etudiant.demo.service.cours;

import com.plateforme_etudiant.demo.dto.CourseRequestDTO;
import com.plateforme_etudiant.demo.dto.CourseResponseDTO;
import com.plateforme_etudiant.demo.model.*;
import com.plateforme_etudiant.demo.model.enums.TypeContenu;
import com.plateforme_etudiant.demo.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class CourseCreationService {

    private static final Logger log = LoggerFactory.getLogger(CourseCreationService.class);

    @Autowired
    private CoursRepository coursRepository;

    @Autowired
    private SectionRepository sectionRepository;

    @Autowired
    private ChapitreRepository chapitreRepository;

    @Autowired
    private ContenuItemRepository contenuItemRepository;

    @Autowired
    private ProfesseurRepository professeurRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private SlugGeneratorService slugGeneratorService;

    @Transactional
    public CourseResponseDTO creerCours(CourseRequestDTO request, Long professeurId) {
        log.info("🚀 Création du cours: {}", request.getTitre());

        // Récupérer le professeur
        Professeur professeur = professeurRepository.findById(professeurId)
                .orElseThrow(() -> new RuntimeException("Professeur non trouvé avec ID: " + professeurId));

        // Générer le slug à partir du titre
        String slug = slugGeneratorService.genererSlugCours(request.getTitre());

        // Créer le cours
        Cours cours = new Cours();
        cours.setTitre(request.getTitre());
        cours.setSlug(slug);  // ← AJOUT IMPORTANT
        cours.setDescriptionCourte(request.getDescriptionCourte());
        cours.setDescription(request.getDescription());
        cours.setDureeEstimee(request.getDureeEstimee() != null ? request.getDureeEstimee() : 0);
        cours.setImageCouverture(request.getImageCouverture());
        cours.setPublie(request.isPublie());
        cours.setProfesseur(professeur);
        cours.setDateCreation(LocalDateTime.now());
        cours.setNombreApprenants(0);

        // Ajouter la catégorie si spécifiée
        if (request.getCategorieId() != null) {
            Categorie categorie = categoryRepository.findById(request.getCategorieId()).orElse(null);
            cours.setCategorie(categorie);
        }

        // Sauvegarder le cours
        cours = coursRepository.save(cours);
        log.info("✅ Cours créé avec ID: {} et slug: {}", cours.getId(), cours.getSlug());

        // Créer les sections, chapitres et contenus
        if (request.getSections() != null && !request.getSections().isEmpty()) {
            for (int i = 0; i < request.getSections().size(); i++) {
                CourseRequestDTO.SectionDTO sectionDTO = request.getSections().get(i);
                creerSection(cours, sectionDTO, i);
            }
        }

        return convertToResponseDTO(cours);
    }

    private void creerSection(Cours cours, CourseRequestDTO.SectionDTO sectionDTO, int ordre) {
        log.info("   📁 Création section: {}", sectionDTO.getTitre());

        Section section = new Section();
        section.setTitre(sectionDTO.getTitre());
        section.setDescription(sectionDTO.getDescription() != null ? sectionDTO.getDescription() : "");
        section.setOrdre(ordre);
        section.setCours(cours);

        section = sectionRepository.save(section);

        // Créer les chapitres de la section
        if (sectionDTO.getChapitres() != null && !sectionDTO.getChapitres().isEmpty()) {
            for (int i = 0; i < sectionDTO.getChapitres().size(); i++) {
                CourseRequestDTO.ChapitreDTO chapitreDTO = sectionDTO.getChapitres().get(i);
                creerChapitre(section, null, chapitreDTO, i);
            }
        }
    }

    private void creerChapitre(Section section, Chapitre parentChapitre, CourseRequestDTO.ChapitreDTO chapitreDTO, int ordre) {
        log.info("      📖 Création chapitre: {}", chapitreDTO.getTitre());

        Chapitre chapitre = new Chapitre();
        chapitre.setTitre(chapitreDTO.getTitre());
        chapitre.setDescription(chapitreDTO.getDescription() != null ? chapitreDTO.getDescription() : "");
        chapitre.setOrdre(ordre);
        chapitre.setSection(section);
        chapitre.setParentChapitre(parentChapitre);

        chapitre = chapitreRepository.save(chapitre);

        // Créer les contenus du chapitre
        if (chapitreDTO.getContenus() != null && !chapitreDTO.getContenus().isEmpty()) {
            for (int i = 0; i < chapitreDTO.getContenus().size(); i++) {
                CourseRequestDTO.ContenuDTO contenuDTO = chapitreDTO.getContenus().get(i);
                creerContenu(chapitre, contenuDTO, i);
            }
        }

        // Créer les sous-chapitres (si existants)
        if (chapitreDTO.getSousChapitres() != null && !chapitreDTO.getSousChapitres().isEmpty()) {
            for (int i = 0; i < chapitreDTO.getSousChapitres().size(); i++) {
                CourseRequestDTO.ChapitreDTO sousChapitreDTO = chapitreDTO.getSousChapitres().get(i);
                creerChapitre(section, chapitre, sousChapitreDTO, i);
            }
        }
    }

    private void creerContenu(Chapitre chapitre, CourseRequestDTO.ContenuDTO contenuDTO, int ordre) {
        log.info("         📄 Création contenu: titre={}, type={}", contenuDTO.getTitre(), contenuDTO.getTypeContenu());

        ContenuItem contenu = new ContenuItem();
        contenu.setTitre(contenuDTO.getTitre());
        contenu.setOrdre(ordre);
        contenu.setChapitre(chapitre);
        contenu.setSection(chapitre.getSection());
        contenu.setApercuGratuit(contenuDTO.getApercuGratuit() != null && contenuDTO.getApercuGratuit());

        String typeStr = contenuDTO.getTypeContenu() != null ? contenuDTO.getTypeContenu() : "TEXTE";
        TypeContenu type = TypeContenu.valueOf(typeStr);
        contenu.setTypeContenu(type);

        switch (type) {
            case VIDEO:
                contenu.setVideoUrl(contenuDTO.getVideoUrl());
                log.info("            Vidéo URL: {}", contenuDTO.getVideoUrl());
                break;
            case TEXTE:
                contenu.setContenuTexte(contenuDTO.getContenuTexte());
                log.info("            Texte sauvegardé");
                break;
            case LIEN:
                contenu.setLienExterne(contenuDTO.getLienExterne());
                contenu.setLienTexte(contenuDTO.getLienTexte());
                log.info("            Lien: {}", contenuDTO.getLienExterne());
                break;
            case PDF:
            case IMAGE:
                contenu.setFichierUrl(contenuDTO.getFichierUrl());
                log.info("            Fichier: {}", contenuDTO.getFichierUrl());
                break;
            default:
                break;
        }

        contenuItemRepository.save(contenu);
        log.info("         ✅ Contenu sauvegardé");
    }
    private CourseResponseDTO convertToResponseDTO(Cours cours) {
        CourseResponseDTO dto = new CourseResponseDTO();
        dto.setId(cours.getId());
        dto.setTitre(cours.getTitre());
        dto.setSlug(cours.getSlug());
        dto.setDescriptionCourte(cours.getDescriptionCourte());
        dto.setDescription(cours.getDescription());
        dto.setDureeEstimee(cours.getDureeEstimee());
        dto.setImageCouverture(cours.getImageCouverture());
        dto.setPublie(cours.getPublie());
        dto.setNombreApprenants(cours.getNombreApprenants() != null ? cours.getNombreApprenants() : 0);
        dto.setDateCreation(cours.getDateCreation());

        // Configurer les informations du professeur
        if (cours.getProfesseur() != null) {
            CourseResponseDTO.ProfesseurInfo professeurInfo = new CourseResponseDTO.ProfesseurInfo();
            professeurInfo.setId(cours.getProfesseur().getId());
            professeurInfo.setSpecialite(cours.getProfesseur().getSpecialite());
            professeurInfo.setBiographie(cours.getProfesseur().getBiographie());

            if (cours.getProfesseur().getUtilisateur() != null) {
                professeurInfo.setNomComplet(cours.getProfesseur().getUtilisateur().getPrenom() + " " +
                        cours.getProfesseur().getUtilisateur().getNom());
                professeurInfo.setEmail(cours.getProfesseur().getUtilisateur().getEmail());
            }
            dto.setProfesseur(professeurInfo);
        }

        // Configurer les informations de la catégorie
        if (cours.getCategorie() != null) {
            CourseResponseDTO.CategorieInfo categorieInfo = new CourseResponseDTO.CategorieInfo();
            categorieInfo.setId(cours.getCategorie().getId());
            categorieInfo.setNom(cours.getCategorie().getNom());
            categorieInfo.setSlug(cours.getCategorie().getSlug());
            dto.setCategorie(categorieInfo);
        }

        return dto;
    }
}