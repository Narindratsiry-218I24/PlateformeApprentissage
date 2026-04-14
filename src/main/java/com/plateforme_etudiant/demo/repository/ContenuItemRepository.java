package com.plateforme_etudiant.demo.repository;

import com.plateforme_etudiant.demo.model.ContenuItem;
import com.plateforme_etudiant.demo.model.Chapitre;
import com.plateforme_etudiant.demo.model.enums.TypeContenu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
public interface ContenuItemRepository extends JpaRepository<ContenuItem, Long> {

    // ==================== RECHERCHE ====================

    List<ContenuItem> findByChapitreOrderByOrdre(Chapitre chapitre);
    List<ContenuItem> findByChapitreIdOrderByOrdre(Long chapitreId);

    List<ContenuItem> findByChapitreAndTypeContenuOrderByOrdre(Chapitre chapitre, TypeContenu type);
    List<ContenuItem> findByChapitreIdAndTypeContenuOrderByOrdre(Long chapitreId, TypeContenu type);

    @Query("SELECT ci FROM ContenuItem ci WHERE ci.section.id = :sectionId ORDER BY ci.chapitre.ordre, ci.ordre")
    List<ContenuItem> findBySectionIdOrderByChapitreAndOrdre(@Param("sectionId") Long sectionId);

    @Query("SELECT ci FROM ContenuItem ci WHERE ci.section.cours.id = :coursId ORDER BY ci.section.ordre, ci.chapitre.ordre, ci.ordre")
    List<ContenuItem> findByCoursIdOrderByHierarchie(@Param("coursId") Long coursId);

    // Compter les contenus par cours
    @Query("SELECT COUNT(ci) FROM ContenuItem ci WHERE ci.section.cours.id = :coursId")
    Long countByCoursId(@Param("coursId") Long coursId);

    @Query("SELECT ci FROM ContenuItem ci WHERE ci.section.cours.id = :coursId AND ci.typeContenu = :type ORDER BY ci.section.ordre, ci.chapitre.ordre, ci.ordre")
    List<ContenuItem> findByCoursIdAndTypeContenu(@Param("coursId") Long coursId, @Param("type") TypeContenu type);

    @Query("SELECT ci FROM ContenuItem ci WHERE ci.apercuGratuit = true AND ci.typeContenu = :type")
    List<ContenuItem> findApercusGratuitsByType(@Param("type") TypeContenu type);

    @Query("SELECT ci FROM ContenuItem ci WHERE ci.apercuGratuit = true AND ci.section.cours.id = :coursId")
    List<ContenuItem> findApercusGratuitsByCoursId(@Param("coursId") Long coursId);

    @Query("SELECT ci FROM ContenuItem ci WHERE ci.titre LIKE %:keyword% OR ci.description LIKE %:keyword%")
    List<ContenuItem> searchByKeyword(@Param("keyword") String keyword);

    @Query("SELECT ci FROM ContenuItem ci WHERE ci.typeContenu = :type AND (ci.titre LIKE %:keyword% OR ci.description LIKE %:keyword%)")
    List<ContenuItem> searchByKeywordAndType(@Param("keyword") String keyword, @Param("type") TypeContenu type);

    @Query("SELECT COUNT(ci) FROM ContenuItem ci WHERE ci.typeContenu = :type")
    long countByTypeContenu(@Param("type") TypeContenu type);

    @Query("SELECT ci.typeContenu, COUNT(ci) FROM ContenuItem ci GROUP BY ci.typeContenu")
    List<Object[]> countByTypeContenuGrouped();

    boolean existsByChapitreIdAndOrdre(Long chapitreId, Integer ordre);

    @Query("SELECT MAX(ci.ordre) FROM ContenuItem ci WHERE ci.chapitre.id = :chapitreId")
    Optional<Integer> findMaxOrdreByChapitreId(@Param("chapitreId") Long chapitreId);

    // ==================== SUPPRESSION ====================

    @Transactional
    @Modifying
    @Query("DELETE FROM ContenuItem ci WHERE ci.section.cours.id = :coursId")
    void deleteByCoursId(@Param("coursId") Long coursId);

    @Transactional
    @Modifying
    @Query("DELETE FROM ContenuItem ci WHERE ci.chapitre.id = :chapitreId")
    void deleteByChapitreId(@Param("chapitreId") Long chapitreId);

    @Transactional
    @Modifying
    @Query("DELETE FROM ContenuItem ci WHERE ci.section.id = :sectionId")
    void deleteBySectionId(@Param("sectionId") Long sectionId);

    @Transactional
    @Modifying
    @Query("DELETE FROM ContenuItem ci WHERE ci.id = :id")
    void deleteByIdWithQuery(@Param("id") Long id);
}