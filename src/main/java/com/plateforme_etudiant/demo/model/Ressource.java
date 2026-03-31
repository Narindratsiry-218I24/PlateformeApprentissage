package com.plateforme_etudiant.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.plateforme_etudiant.demo.model.enums.TypeRessource;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entité représentant une ressource additionnelle (fichier, lien, etc.)
 */
@Entity
@Table(name = "ressources")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Ressource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // MODIFICATION : Pointer vers ContenuItem au lieu de Lecon
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contenu_item_id", nullable = false)
    @JsonIgnore
    private ContenuItem contenuItem;

    @Column(length = 200, nullable = false)
    private String titre;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_ressource", length = 20, nullable = false)
    private TypeRessource typeRessource; // PDF, VIDEO, LIEN, IMAGE, etc.

    @Column(name = "url", length = 500)
    private String url;

    @Column(name = "fichier_nom", length = 255)
    private String fichierNom;

    @Column(name = "taille_fichier")
    private Long tailleFichier; // en bytes

    @Column(columnDefinition = "TEXT")
    private String description;

    @CreationTimestamp
    @Column(name = "date_ajout", updatable = false)
    private LocalDateTime dateAjout;

    /**
     * Vérifie si c'est un fichier téléchargeable
     */
    public boolean estFichier() {
        return typeRessource == TypeRessource.PDF ||
                typeRessource == TypeRessource.VIDEO ||
                typeRessource == TypeRessource.IMAGE ||
                typeRessource == TypeRessource.DOCUMENT;
    }
}

