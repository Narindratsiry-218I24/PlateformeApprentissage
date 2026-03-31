// SlugGeneratorService.java
package com.plateforme_etudiant.demo.service.cours;

import com.plateforme_etudiant.demo.repository.CoursRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class SlugGeneratorService {

    private static final Logger log = LoggerFactory.getLogger(SlugGeneratorService.class);

    private final CoursRepository coursRepository;

    public SlugGeneratorService(CoursRepository coursRepository) {
        this.coursRepository = coursRepository;
    }

    public String genererSlugCours(String titre) {
        String slug = titre.toLowerCase()
                .replaceAll("[éèêë]", "e")
                .replaceAll("[àâä]", "a")
                .replaceAll("[îï]", "i")
                .replaceAll("[ôö]", "o")
                .replaceAll("[ùûü]", "u")
                .replaceAll("[ç]", "c")
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-")
                .trim();

        String slugOriginal = slug;
        int compteur = 1;
        while (coursRepository.existsBySlug(slug)) {
            slug = slugOriginal + "-" + compteur++;
            log.debug("Slug déjà existant, nouveau slug généré: {}", slug);
        }

        log.debug("Slug généré pour '{}': {}", titre, slug);
        return slug;
    }
}