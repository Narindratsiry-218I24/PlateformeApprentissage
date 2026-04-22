package com.plateforme_etudiant.demo.repository;

import com.plateforme_etudiant.demo.model.Cours;
import com.plateforme_etudiant.demo.model.Professeur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
public interface CoursRepository extends JpaRepository<Cours, Long> {

    // Recherche
    Optional<Cours> findBySlug(String slug);

    Optional<Cours> findByIdAndProfesseurId(Long id, Long professeurId);

    List<Cours> findByProfesseur(Professeur professeur);

    List<Cours> findByProfesseurIdOrderByDateCreationDesc(Long professeurId);

    List<Cours> findByPublieTrue();

    List<Cours> findByPublieTrueOrderByDateCreationDesc();

    List<Cours> findByCategorieId(Long categorieId);

    List<Cours> findByCategorieIdAndPublieTrue(Long categorieId);

    boolean existsBySlug(String slug);

    @Query("SELECT c FROM Cours c WHERE c.titre LIKE %:keyword% OR c.description LIKE %:keyword%")
    List<Cours> searchByKeyword(@Param("keyword") String keyword);

    @Query("SELECT c FROM Cours c WHERE c.publie = true ORDER BY c.nombreApprenants DESC")
    List<Cours> findTopCoursesByPopularite();

    @Query("SELECT COUNT(i) FROM Inscription i WHERE i.cours.id = :coursId")
    long countInscriptionsByCoursId(@Param("coursId") Long coursId);

    // Suppression
    @Transactional
    @Modifying
    @Query("DELETE FROM Cours c WHERE c.id = :id")
    void deleteByIdWithQuery(@Param("id") Long id);
}
