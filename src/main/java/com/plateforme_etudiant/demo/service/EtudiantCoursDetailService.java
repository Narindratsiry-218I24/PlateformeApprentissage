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
    private final ProgressionService progressionService;

    public EtudiantCoursDetailService(CoursRepository coursRepository,
                                      SectionRepository sectionRepository,
                                      ChapitreRepository chapitreRepository,
                                      ContenuItemRepository contenuItemRepository,
                                      ProgressionRepository progressionRepository,
                                      ProgressionService progressionService) {
        this.coursRepository = coursRepository;
        this.sectionRepository = sectionRepository;
        this.chapitreRepository = chapitreRepository;
        this.contenuItemRepository = contenuItemRepository;
        this.progressionRepository = progressionRepository;
        this.progressionService = progressionService;
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

        int progressionGlobale = progressionService.calculerProgressionCours(etudiantId, coursId);
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

        // Vérifier si le chapitre est complet (tous ses contenus sont complétés)
        boolean chapitreComplete = isChapitreComplete(chapitre.getId(), etudiantId);
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

        // === CHAMPS IMPORTANTS À INCLURE ===
        dto.setVideoUrl(contenu.getVideoUrl());
        dto.setContenuTexte(contenu.getContenuTexte());
        dto.setFichierUrl(contenu.getFichierUrl());
        dto.setLienExterne(contenu.getLienExterne());
        dto.setLienTexte(contenu.getLienTexte());

        // Champ pour le contenu principal (texte riche)
        if (contenu.getContenuPrincipal() != null) {
            dto.setContenuPrincipal(contenu.getContenuPrincipal());
        }

        // Vérifier si le contenu est complété par l'étudiant
        boolean contenuComplete = progressionRepository.isContenuComplete(contenu.getId(), etudiantId);
        dto.setEstComplete(contenuComplete);

        // Générer le HTML complet du contenu
        dto.setHtmlContent(genererHtmlContenu(contenu));

        log.debug("Contenu converti: id={}, titre={}, type={}, videoUrl={}, texte={}",
                contenu.getId(), contenu.getTitre(), contenu.getTypeContenu(),
                contenu.getVideoUrl(), contenu.getContenuTexte() != null ? "present" : "null");

        return dto;
    }

    private boolean isChapitreComplete(Long chapitreId, Long etudiantId) {
        List<ContenuItem> contenus = contenuItemRepository.findByChapitreIdOrderByOrdre(chapitreId);
        boolean hasContenu = !contenus.isEmpty();

        for (ContenuItem contenu : contenus) {
            boolean estComplete = progressionRepository.isContenuComplete(contenu.getId(), etudiantId);
            if (!estComplete) return false;
        }

        List<Chapitre> sousChapitres = chapitreRepository.findByParentChapitreIdOrderByOrdre(chapitreId);
        for (Chapitre sousChapitre : sousChapitres) {
            hasContenu = true;
            if (!isChapitreComplete(sousChapitre.getId(), etudiantId)) {
                return false;
            }
        }

        return hasContenu;
    }

    private String genererHtmlContenu(ContenuItem contenu) {
        String titre = contenu.getTitre() != null ? contenu.getTitre() : "";

        switch (contenu.getTypeContenu()) {
            case VIDEO:
                String videoUrl = contenu.getVideoUrl() != null ? contenu.getVideoUrl() : "";
                if (videoUrl.isEmpty()) {
                    return "<div class='text-center text-gray-500 py-10'><span class='material-symbols-outlined text-4xl mb-2'>warning</span><p>URL de la vidéo non disponible</p></div>";
                }
                return "<div class='video-container'>" +
                        "<h3 class='text-lg font-bold mb-3'>" + escapeHtml(titre) + "</h3>" +
                        "<video controls class='w-full rounded-lg' controlsList='nodownload'>" +
                        "<source src='" + escapeAttr(videoUrl) + "' type='video/mp4'>" +
                        "Votre navigateur ne supporte pas la vidéo." +
                        "</video>" +
                        "</div>";

            case TEXTE:
                String texte = contenu.getContenuTexte() != null ? contenu.getContenuTexte() : "";
                if (texte.isEmpty() && contenu.getContenuPrincipal() != null) {
                    texte = contenu.getContenuPrincipal();
                }
                if (texte.isEmpty()) {
                    return "<div class='text-center text-gray-500 py-10'><span class='material-symbols-outlined text-4xl mb-2'>edit_note</span><p>Contenu texte non disponible</p></div>";
                }
                return "<div class='text-content prose max-w-none'>" +
                        "<h3 class='text-lg font-bold mb-3'>" + escapeHtml(titre) + "</h3>" +
                        "<div class='mt-2'>" + texte + "</div>" +
                        "</div>";

            case PDF:
                String pdfUrl = contenu.getFichierUrl() != null ? contenu.getFichierUrl() : "";
                if (pdfUrl.isEmpty()) {
                    return "<div class='text-center text-gray-500 py-10'><span class='material-symbols-outlined text-4xl mb-2'>picture_as_pdf</span><p>Fichier PDF non disponible</p></div>";
                }
                return "<div class='pdf-container'>" +
                        "<h3 class='text-lg font-bold mb-3'>" + escapeHtml(titre) + "</h3>" +
                        "<iframe src='" + escapeAttr(pdfUrl) + "' class='w-full h-[600px] rounded-lg'></iframe>" +
                        "<a href='" + escapeAttr(pdfUrl) + "' download class='mt-4 inline-block bg-primary text-white px-4 py-2 rounded-lg hover:bg-primary/90'>" +
                        "Télécharger le PDF" +
                        "</a>" +
                        "</div>";

            case IMAGE:
                String imageUrl = contenu.getFichierUrl() != null ? contenu.getFichierUrl() : "";
                if (imageUrl.isEmpty()) {
                    return "<div class='text-center text-gray-500 py-10'><span class='material-symbols-outlined text-4xl mb-2'>image</span><p>Image non disponible</p></div>";
                }
                return "<div class='image-container'>" +
                        "<h3 class='text-lg font-bold mb-3'>" + escapeHtml(titre) + "</h3>" +
                        "<img src='" + escapeAttr(imageUrl) + "' alt='" + escapeAttr(titre) + "' class='w-full rounded-lg'>" +
                        "</div>";

            case LIEN:
                String lienUrl = contenu.getLienExterne() != null ? contenu.getLienExterne() : "";
                String lienTexte = contenu.getLienTexte() != null ? contenu.getLienTexte() : titre;
                if (lienUrl.isEmpty()) {
                    return "<div class='text-center text-gray-500 py-10'><span class='material-symbols-outlined text-4xl mb-2'>link_off</span><p>Lien non disponible</p></div>";
                }
                return "<div class='link-container'>" +
                        "<h3 class='text-lg font-bold mb-3'>" + escapeHtml(titre) + "</h3>" +
                        "<a href='" + escapeAttr(lienUrl) + "' target='_blank' rel='noopener noreferrer' class='text-primary hover:underline flex items-center gap-2'>" +
                        "<span class='material-symbols-outlined'>open_in_new</span>" +
                        escapeHtml(lienTexte) +
                        "</a>" +
                        "</div>";

            default:
                return "<div class='text-center text-gray-500 py-10'>" +
                        "<span class='material-symbols-outlined text-4xl mb-2'>help</span>" +
                        "<p>Type de contenu: " + contenu.getTypeContenu() + "</p>" +
                        "<p class='text-sm'>" + escapeHtml(titre) + "</p>" +
                        "</div>";
        }
    }

    private String escapeHtml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }

    private String escapeAttr(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}
