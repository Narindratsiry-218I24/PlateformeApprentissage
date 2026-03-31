package com.plateforme_etudiant.demo.repository;

import com.plateforme_etudiant.demo.model.Chapitre;
import com.plateforme_etudiant.demo.model.Section;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
public interface ChapitreRepository extends JpaRepository<Chapitre, Long> {

    // ==================== RECHERCHE ====================

    // Chapitres par section
    List<Chapitre> findBySectionOrderByOrdre(Section section);

    List<Chapitre> findBySectionIdOrderByOrdre(Long sectionId);

    // Chapitres racine (sans parent)
    @Query("SELECT c FROM Chapitre c WHERE c.section = :section AND c.parentChapitre IS NULL ORDER BY c.ordre")
    List<Chapitre> findByParentChapitreIsNullOrderByOrdre(@Param("section") Section section);

    @Query("SELECT c FROM Chapitre c WHERE c.section.id = :sectionId AND c.parentChapitre IS NULL ORDER BY c.ordre")
    List<Chapitre> findByParentChapitreIsNullAndSectionIdOrderByOrdre(@Param("sectionId") Long sectionId);

    // Sous-chapitres
    List<Chapitre> findByParentChapitreOrderByOrdre(Chapitre parentChapitre);

    @Query("SELECT c FROM Chapitre c WHERE c.parentChapitre.id = :parentId ORDER BY c.ordre")
    List<Chapitre> findByParentChapitreIdOrderByOrdre(@Param("parentId") Long parentId);

    // Recherche hiérarchique
    @Query("SELECT c FROM Chapitre c WHERE c.section.cours.id = :coursId ORDER BY c.section.ordre, c.ordre")
    List<Chapitre> findAllByCoursIdOrderByHierarchie(@Param("coursId") Long coursId);

    @Query("SELECT c FROM Chapitre c WHERE c.titre LIKE %:keyword%")
    List<Chapitre> searchByTitre(@Param("keyword") String keyword);

    // Compteurs
    @Query("SELECT COUNT(ci) FROM Chapitre c JOIN c.contenus ci WHERE c.id = :chapitreId")
    long countContenusByChapitreId(@Param("chapitreId") Long chapitreId);

    @Query("SELECT COUNT(sc) FROM Chapitre c JOIN c.sousChapitres sc WHERE c.id = :chapitreId")
    long countSousChapitresByChapitreId(@Param("chapitreId") Long chapitreId);

    // Vérifications
    boolean existsByParentChapitreId(Long parentId);

    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Chapitre c WHERE c.parentChapitre.id = :parentId AND c.ordre = :ordre")
    boolean existsByParentIdAndOrdre(@Param("parentId") Long parentId, @Param("ordre") Integer ordre);

    // ==================== SUPPRESSION ====================

    @Transactional
    @Modifying
    @Query("DELETE FROM Chapitre c WHERE c.section.cours.id = :coursId")
    void deleteByCoursId(@Param("coursId") Long coursId);

    @Transactional
    @Modifying
    @Query("DELETE FROM Chapitre c WHERE c.section.id = :sectionId")
    void deleteBySectionId(@Param("sectionId") Long sectionId);

    @Transactional
    @Modifying
    @Query("DELETE FROM Chapitre c WHERE c.parentChapitre.id = :parentId")
    void deleteByParentChapitreId(@Param("parentId") Long parentId);

    @Transactional
    @Modifying
    @Query("DELETE FROM Chapitre c WHERE c.id = :id")
    void deleteByIdWithQuery(@Param("id") Long id);
}