package com.plateforme_etudiant.demo.repository;

import com.plateforme_etudiant.demo.model.Ressource;
import com.plateforme_etudiant.demo.model.enums.TypeRessource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Repository
public interface RessourceRepository extends JpaRepository<Ressource, Long> {

    // ==================== RECHERCHE ====================

    List<Ressource> findByContenuItemIdOrderByDateAjoutDesc(Long contenuItemId);

    List<Ressource> findByTypeRessource(TypeRessource type);

    @Query("SELECT r FROM Ressource r WHERE r.contenuItem.section.cours.id = :coursId")
    List<Ressource> findByCoursId(@Param("coursId") Long coursId);

    @Query("SELECT r FROM Ressource r WHERE r.contenuItem.section.cours.id = :coursId AND r.typeRessource = :type")
    List<Ressource> findByCoursIdAndType(@Param("coursId") Long coursId, @Param("type") TypeRessource type);

    @Query("SELECT r FROM Ressource r WHERE r.contenuItem.chapitre.id = :chapitreId")
    List<Ressource> findByChapitreId(@Param("chapitreId") Long chapitreId);

    // Compteurs
    @Query("SELECT COUNT(r) FROM Ressource r WHERE r.contenuItem.id = :contenuItemId")
    long countByContenuItemId(@Param("contenuItemId") Long contenuItemId);

    // ==================== SUPPRESSION ====================

    @Transactional
    @Modifying
    @Query("DELETE FROM Ressource r WHERE r.contenuItem.section.cours.id = :coursId")
    void deleteByCoursId(@Param("coursId") Long coursId);

    @Transactional
    @Modifying
    @Query("DELETE FROM Ressource r WHERE r.contenuItem.id = :contenuItemId")
    void deleteByContenuItemId(@Param("contenuItemId") Long contenuItemId);

    @Transactional
    @Modifying
    @Query("DELETE FROM Ressource r WHERE r.contenuItem.chapitre.id = :chapitreId")
    void deleteByChapitreId(@Param("chapitreId") Long chapitreId);

    @Transactional
    @Modifying
    @Query("DELETE FROM Ressource r WHERE r.id = :id")
    void deleteByIdWithQuery(@Param("id") Long id);
}