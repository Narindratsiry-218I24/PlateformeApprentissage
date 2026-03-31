// CourseConversionService.java
package com.plateforme_etudiant.demo.service.cours;

import com.plateforme_etudiant.demo.dto.CourseResponseDTO;
import com.plateforme_etudiant.demo.model.*;
import com.plateforme_etudiant.demo.repository.ChapitreRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CourseConversionService {

    private static final Logger log = LoggerFactory.getLogger(CourseConversionService.class);

    private final ChapitreRepository chapitreRepository;

    public CourseConversionService(ChapitreRepository chapitreRepository) {
        this.chapitreRepository = chapitreRepository;
    }

    /**
     * Convertir un cours en DTO
     */
    public CourseResponseDTO convertirEnReponse(Cours cours) {
        log.debug("Conversion du cours ID: {} en DTO", cours.getId());

        CourseResponseDTO dto = new CourseResponseDTO();
        dto.setId(cours.getId());
        dto.setTitre(cours.getTitre());
        dto.setSlug(cours.getSlug());
        dto.setDescription(cours.getDescription());
        dto.setDescriptionCourte(cours.getDescriptionCourte());
        dto.setImageCouverture(cours.getImageCouverture());
        dto.setPublie(cours.getPublie());
        dto.setDureeEstimee(cours.getDureeEstimee());
        dto.setNombreApprenants(cours.getNombreApprenants());
        dto.setDateCreation(cours.getDateCreation());
        dto.setDatePublication(cours.getDatePublication());

        dto.setCategorie(convertirCategorie(cours.getCategorie()));
        dto.setProfesseur(convertirProfesseur(cours.getProfesseur()));

        if (cours.getSections() != null) {
            List<CourseResponseDTO.SectionInfo> sectionsDTO = cours.getSections().stream()
                    .map(this::convertirSectionEnReponse)
                    .collect(Collectors.toList());
            dto.setSections(sectionsDTO);
        }

        return dto;
    }

    private CourseResponseDTO.CategorieInfo convertirCategorie(Categorie categorie) {
        if (categorie == null) return null;
        CourseResponseDTO.CategorieInfo info = new CourseResponseDTO.CategorieInfo();
        info.setId(categorie.getId());
        info.setNom(categorie.getNom());
        info.setSlug(categorie.getSlug());
        return info;
    }

    private CourseResponseDTO.ProfesseurInfo convertirProfesseur(Professeur professeur) {
        if (professeur == null) return null;
        CourseResponseDTO.ProfesseurInfo info = new CourseResponseDTO.ProfesseurInfo();
        info.setId(professeur.getId());
        info.setSpecialite(professeur.getSpecialite());
        info.setBiographie(professeur.getBiographie());

        if (professeur.getUtilisateur() != null) {
            Utilisateur user = professeur.getUtilisateur();
            info.setNomComplet(user.getNomComplet());
            info.setEmail(user.getEmail());
        }
        return info;
    }

    private CourseResponseDTO.SectionInfo convertirSectionEnReponse(Section section) {
        CourseResponseDTO.SectionInfo info = new CourseResponseDTO.SectionInfo();
        info.setId(section.getId());
        info.setTitre(section.getTitre());
        info.setDescription(section.getDescription());
        info.setOrdre(section.getOrdre());

        // Calculer le nombre total de contenus
        int totalContenus = 0;
        List<Chapitre> chapitresRacine = chapitreRepository.findByParentChapitreIsNullAndSectionIdOrderByOrdre(section.getId());
        for (Chapitre chapitre : chapitresRacine) {
            totalContenus += chapitre.getNombreTotalContenus();
        }
        info.setNombreTotalContenus(totalContenus);

        if (chapitresRacine != null) {
            info.setChapitres(chapitresRacine.stream()
                    .map(this::convertirChapitreEnReponse)
                    .collect(Collectors.toList()));
        }

        return info;
    }

    private CourseResponseDTO.ChapitreInfo convertirChapitreEnReponse(Chapitre chapitre) {
        CourseResponseDTO.ChapitreInfo info = new CourseResponseDTO.ChapitreInfo();
        info.setId(chapitre.getId());
        info.setTitre(chapitre.getTitre());
        info.setDescription(chapitre.getDescription());
        info.setOrdre(chapitre.getOrdre());
        info.setNiveauProfondeur(chapitre.getNiveauProfondeur());
        info.setADesSousChapitres(chapitre.aDesSousChapitres());
        info.setNombreTotalContenus(chapitre.getNombreTotalContenus());

        if (chapitre.getSousChapitres() != null) {
            info.setSousChapitres(chapitre.getSousChapitres().stream()
                    .map(this::convertirChapitreEnReponse)
                    .collect(Collectors.toList()));
        }

        if (chapitre.getContenus() != null) {
            info.setContenus(chapitre.getContenus().stream()
                    .map(this::convertirContenuEnReponse)
                    .collect(Collectors.toList()));
        }

        return info;
    }

    private CourseResponseDTO.ContenuItemInfo convertirContenuEnReponse(ContenuItem contenu) {
        CourseResponseDTO.ContenuItemInfo info = new CourseResponseDTO.ContenuItemInfo();
        info.setId(contenu.getId());
        info.setTitre(contenu.getTitre());
        info.setDescription(contenu.getDescription());
        info.setTypeContenu(contenu.getTypeContenu().toString());
        info.setContenuPrincipal(contenu.getContenuPrincipal());
        info.setOrdre(contenu.getOrdre());
        info.setApercuGratuit(contenu.getApercuGratuit());
        info.setPleineLargeur(contenu.getPleineLargeur());

        // Champs spécifiques
        info.setVideoUrl(contenu.getVideoUrl());
        info.setDureeVideo(contenu.getDureeVideo());
        info.setContenuTexte(contenu.getContenuTexte());
        info.setFichierUrl(contenu.getFichierUrl());
        info.setImageLegende(contenu.getImageLegende());
        info.setImageLargeur(contenu.getImageLargeur());
        info.setImageHauteur(contenu.getImageHauteur());
        info.setLienExterne(contenu.getLienExterne());
        info.setLienTexte(contenu.getLienTexte());

        info.setHtmlContent(genererHtmlPourAffichage(contenu));

        return info;
    }

    private String genererHtmlPourAffichage(ContenuItem contenu) {
        switch (contenu.getTypeContenu()) {
            case VIDEO:
                int minutes = (contenu.getDureeVideo() != null ? contenu.getDureeVideo() : 0) / 60;
                int secondes = (contenu.getDureeVideo() != null ? contenu.getDureeVideo() : 0) % 60;
                return String.format(
                        "<div class='video-container %s'><video controls width='100%%'><source src='%s' type='video/mp4'></video><div class='video-duration'>Durée: %d:%02d</div></div>",
                        contenu.getPleineLargeur() ? "full-width" : "", contenu.getVideoUrl(), minutes, secondes);
            case TEXTE:
                return String.format("<div class='text-content %s'>%s</div>", contenu.getPleineLargeur() ? "full-width" : "", contenu.getContenuTexte());
            case PDF:
                return String.format("<div class='pdf-container %s'><iframe src='%s' width='100%%' height='500px'></iframe><a href='%s' download>Télécharger le PDF</a></div>",
                        contenu.getPleineLargeur() ? "full-width" : "", contenu.getFichierUrl(), contenu.getFichierUrl());
            case PRESENTATION:
                return String.format("<div class='presentation-container %s'><a href='%s'>Télécharger la présentation</a></div>",
                        contenu.getPleineLargeur() ? "full-width" : "", contenu.getFichierUrl());
            case IMAGE:
                return String.format("<div class='image-container %s'><img src='%s' alt='%s' /><div class='image-caption'>%s</div></div>",
                        contenu.getPleineLargeur() ? "full-width" : "", contenu.getFichierUrl(), contenu.getTitre(),
                        contenu.getImageLegende() != null ? contenu.getImageLegende() : "");
            case LIEN:
                String lienTexte = contenu.getLienTexte() != null ? contenu.getLienTexte() : contenu.getTitre();
                return String.format("<div class='link-container %s'><a href='%s' target='_blank'>%s</a></div>",
                        contenu.getPleineLargeur() ? "full-width" : "", contenu.getLienExterne(), lienTexte);
            default:
                return "";
        }
    }
}