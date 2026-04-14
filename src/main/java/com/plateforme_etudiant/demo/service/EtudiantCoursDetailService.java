// service/EtudiantCoursDetailService.java
package com.plateforme_etudiant.demo.service;

import com.plateforme_etudiant.demo.dto.CoursDetailDTO;
import com.plateforme_etudiant.demo.model.*;
import com.plateforme_etudiant.demo.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EtudiantCoursDetailService {

    private static final Logger log = LoggerFactory.getLogger(EtudiantCoursDetailService.class);

    private final CoursRepository coursRepository;
    private final SectionRepository sectionRepository;
    private final ChapitreRepository chapitreRepository;
    private final ContenuItemRepository contenuItemRepository;
    private final ProgressionRepository progressionRepository;

    public EtudiantCoursDetailService(CoursRepository coursRepository,
                                      SectionRepository sectionRepository,
                                      ChapitreRepository chapitreRepository,
                                      ContenuItemRepository contenuItemRepository,
                                      ProgressionRepository progressionRepository) {
        this.coursRepository = coursRepository;
        this.sectionRepository = sectionRepository;
        this.chapitreRepository = chapitreRepository;
        this.contenuItemRepository = contenuItemRepository;
        this.progressionRepository = progressionRepository;
    }

    public CoursDetailDTO getCoursDetail(Long coursId, Long etudiantId) {
        Cours cours = coursRepository.findById(coursId)
                .orElseThrow(() -> new RuntimeException("Cours non trouvé"));

        CoursDetailDTO dto = new CoursDetailDTO();
        dto.setId(cours.getId());
        dto.setTitre(cours.getTitre());
        dto.setSlug(cours.getSlug());
        dto.setDescription(cours.getDescription());
        dto.setDescriptionCourte(cours.getDescriptionCourte());
        dto.setImageCouverture(cours.getImageCouverture());

        int progressionGlobale = progressionRepository.getProgressionByCoursAndEtudiant(coursId, etudiantId);
        dto.setProgression(progressionGlobale);

        List<Section> sections = sectionRepository.findByCoursIdOrderByOrdre(coursId);
        List<CoursDetailDTO.SectionDetailDTO> sectionsDTO = sections.stream()
                .map(section -> convertSection(section, etudiantId))
                .collect(Collectors.toList());
        dto.setSections(sectionsDTO);

        return dto;
    }

    private CoursDetailDTO.SectionDetailDTO convertSection(Section section, Long etudiantId) {
        CoursDetailDTO.SectionDetailDTO dto = new CoursDetailDTO.SectionDetailDTO();
        dto.setId(section.getId());
        dto.setTitre(section.getTitre());
        dto.setDescription(section.getDescription());
        dto.setOrdre(section.getOrdre());

        List<Chapitre> chapitresRacine = chapitreRepository.findByParentChapitreIsNullAndSectionIdOrderByOrdre(section.getId());
        List<CoursDetailDTO.ChapitreDetailDTO> chapitresDTO = chapitresRacine.stream()
                .map(chapitre -> convertChapitre(chapitre, etudiantId))
                .collect(Collectors.toList());
        dto.setChapitres(chapitresDTO);

        return dto;
    }

    private CoursDetailDTO.ChapitreDetailDTO convertChapitre(Chapitre chapitre, Long etudiantId) {
        CoursDetailDTO.ChapitreDetailDTO dto = new CoursDetailDTO.ChapitreDetailDTO();
        dto.setId(chapitre.getId());
        dto.setTitre(chapitre.getTitre());
        dto.setDescription(chapitre.getDescription());
        dto.setOrdre(chapitre.getOrdre());
        dto.setNiveauProfondeur(chapitre.getNiveauProfondeur());

        boolean chapitreComplete = progressionRepository.isChapitreComplete(chapitre.getId(), etudiantId);
        dto.setEstComplete(chapitreComplete);

        if (chapitre.getSousChapitres() != null && !chapitre.getSousChapitres().isEmpty()) {
            List<CoursDetailDTO.ChapitreDetailDTO> sousChapitresDTO = chapitre.getSousChapitres().stream()
                    .map(sc -> convertChapitre(sc, etudiantId))
                    .collect(Collectors.toList());
            dto.setSousChapitres(sousChapitresDTO);
        }

        if (chapitre.getContenus() != null && !chapitre.getContenus().isEmpty()) {
            List<CoursDetailDTO.ContenuDetailDTO> contenusDTO = chapitre.getContenus().stream()
                    .map(contenu -> convertContenu(contenu, etudiantId))
                    .collect(Collectors.toList());
            dto.setContenus(contenusDTO);
        }

        return dto;
    }

    private CoursDetailDTO.ContenuDetailDTO convertContenu(ContenuItem contenu, Long etudiantId) {
        CoursDetailDTO.ContenuDetailDTO dto = new CoursDetailDTO.ContenuDetailDTO();
        dto.setId(contenu.getId());
        dto.setTitre(contenu.getTitre());
        dto.setTypeContenu(contenu.getTypeContenu().toString());
        dto.setOrdre(contenu.getOrdre());
        dto.setVideoUrl(contenu.getVideoUrl());
        dto.setContenuTexte(contenu.getContenuTexte());
        dto.setFichierUrl(contenu.getFichierUrl());
        dto.setLienExterne(contenu.getLienExterne());
        dto.setLienTexte(contenu.getLienTexte());

        boolean contenuComplete = progressionRepository.isContenuComplete(contenu.getId(), etudiantId);
        dto.setEstComplete(contenuComplete);

        // Générer le HTML sans échappement pour l'affichage
        dto.setHtmlContent(genererHtmlContenu(contenu));

        return dto;
    }

    private String genererHtmlContenu(ContenuItem contenu) {
        switch (contenu.getTypeContenu()) {
            case VIDEO:
                String videoUrl = contenu.getVideoUrl() != null ? contenu.getVideoUrl() : "";
                return "<div class='video-container'>" +
                        "<video controls class='w-full rounded-lg'>" +
                        "<source src='" + videoUrl + "' type='video/mp4'>" +
                        "Votre navigateur ne supporte pas la vidéo." +
                        "</video>" +
                        "</div>";
            case TEXTE:
                String texte = contenu.getContenuTexte() != null ? contenu.getContenuTexte() : "";
                return "<div class='prose max-w-none'>" + texte + "</div>";
            case PDF:
                String pdfUrl = contenu.getFichierUrl() != null ? contenu.getFichierUrl() : "";
                return "<div class='pdf-container'>" +
                        "<iframe src='" + pdfUrl + "' class='w-full h-[600px] rounded-lg'></iframe>" +
                        "<a href='" + pdfUrl + "' download class='mt-4 inline-block bg-primary text-white px-4 py-2 rounded-lg'>" +
                        "Télécharger le PDF" +
                        "</a>" +
                        "</div>";
            case LIEN:
                String lienUrl = contenu.getLienExterne() != null ? contenu.getLienExterne() : "#";
                String lienTexte = contenu.getLienTexte() != null ? contenu.getLienTexte() : contenu.getTitre();
                return "<div class='link-container'>" +
                        "<a href='" + lienUrl + "' target='_blank' rel='noopener noreferrer' class='text-primary hover:underline'>" +
                        lienTexte +
                        "</a>" +
                        "</div>";
            case IMAGE:
                String imageUrl = contenu.getFichierUrl() != null ? contenu.getFichierUrl() : "";
                return "<div class='image-container'>" +
                        "<img src='" + imageUrl + "' alt='" + contenu.getTitre() + "' class='w-full rounded-lg'>" +
                        "</div>";
            default:
                return "<p>Contenu non disponible</p>";
        }
    }
}