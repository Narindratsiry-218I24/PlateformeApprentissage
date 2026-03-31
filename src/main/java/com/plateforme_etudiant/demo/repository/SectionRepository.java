package com.plateforme_etudiant.demo.repository;

import com.plateforme_etudiant.demo.model.Section;
import com.plateforme_etudiant.demo.model.Cours;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Repository
public interface SectionRepository extends JpaRepository<Section, Long> {

    // Recherche
    List<Section> findByCoursOrderByOrdre(Cours cours);

    List<Section> findByCoursIdOrderByOrdre(Long coursId);

    @Query("SELECT s FROM Section s WHERE s.cours.id = :coursId ORDER BY s.ordre ASC")
    List<Section> findAllByCoursIdOrderByOrdre(@Param("coursId") Long coursId);

    @Query("SELECT COUNT(c) FROM Section s JOIN s.chapitres c WHERE s.id = :sectionId")
    long countChapitresBySectionId(@Param("sectionId") Long sectionId);

    @Query("SELECT COUNT(ci) FROM Section s JOIN s.chapitres c JOIN c.contenus ci WHERE s.id = :sectionId")
    long countContenusBySectionId(@Param("sectionId") Long sectionId);

    // Suppression
    @Transactional
    @Modifying
    @Query("DELETE FROM Section s WHERE s.cours.id = :coursId")
    void deleteByCoursId(@Param("coursId") Long coursId);

    @Transactional
    @Modifying
    @Query("DELETE FROM Section s WHERE s.id = :id")
    void deleteByIdWithQuery(@Param("id") Long id);
}