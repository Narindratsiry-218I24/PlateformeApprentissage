package com.plateforme_etudiant.demo.repository;

import com.plateforme_etudiant.demo.model.Favori;
import com.plateforme_etudiant.demo.model.Utilisateur;
import com.plateforme_etudiant.demo.model.ContenuItem;
import com.plateforme_etudiant.demo.model.enums.TypeContenu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Repository
public interface FavoriRepository extends JpaRepository<Favori, Long> {

    // ==================== RECHERCHE ====================

    List<Favori> findByApprenantOrderByDateCreationDesc(Utilisateur apprenant);

    List<Favori> findByApprenantIdOrderByDateCreationDesc(Long apprenantId);

    boolean existsByApprenantAndContenuItem(Utilisateur apprenant, ContenuItem contenuItem);

    boolean existsByApprenantIdAndContenuItemId(Long apprenantId, Long contenuItemId);

    @Query("SELECT f FROM Favori f WHERE f.apprenant.id = :apprenantId AND f.contenuItem.typeContenu = :type")
    List<Favori> findByApprenantIdAndTypeContenu(@Param("apprenantId") Long apprenantId, @Param("type") TypeContenu type);

    @Query("SELECT f.contenuItem FROM Favori f WHERE f.apprenant.id = :apprenantId")
    List<ContenuItem> findContenusFavorisByApprenantId(@Param("apprenantId") Long apprenantId);

    // Compteurs
    @Query("SELECT COUNT(f) FROM Favori f WHERE f.contenuItem.id = :contenuItemId")
    long countByContenuItemId(@Param("contenuItemId") Long contenuItemId);

    @Query("SELECT COUNT(f) FROM Favori f WHERE f.apprenant.id = :apprenantId")
    long countByApprenantId(@Param("apprenantId") Long apprenantId);

    // ==================== SUPPRESSION ====================

    @Transactional
    @Modifying
    void deleteByApprenantIdAndContenuItemId(Long apprenantId, Long contenuItemId);

    @Transactional
    @Modifying
    @Query("DELETE FROM Favori f WHERE f.contenuItem.section.cours.id = :coursId")
    void deleteByCoursId(@Param("coursId") Long coursId);

    @Transactional
    @Modifying
    @Query("DELETE FROM Favori f WHERE f.contenuItem.id = :contenuItemId")
    void deleteByContenuItemId(@Param("contenuItemId") Long contenuItemId);

    @Transactional
    @Modifying
    @Query("DELETE FROM Favori f WHERE f.contenuItem.chapitre.id = :chapitreId")
    void deleteByChapitreId(@Param("chapitreId") Long chapitreId);

    @Transactional
    @Modifying
    @Query("DELETE FROM Favori f WHERE f.apprenant.id = :apprenantId")
    void deleteByApprenantId(@Param("apprenantId") Long apprenantId);
}