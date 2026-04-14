package com.plateforme_etudiant.demo.repository;

import com.plateforme_etudiant.demo.model.Progression;
import com.plateforme_etudiant.demo.model.Utilisateur;
import com.plateforme_etudiant.demo.model.ContenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProgressionRepository extends JpaRepository<Progression, Long> {

    // ==================== RECHERCHE ====================

    Optional<Progression> findByApprenantAndContenuItem(Utilisateur apprenant, ContenuItem contenuItem);

    Optional<Progression> findByApprenantIdAndContenuItemId(Long apprenantId, Long contenuItemId);

    List<Progression> findByApprenantIdOrderByDateDebutDesc(Long apprenantId);

    @Query("SELECT p FROM Progression p WHERE p.apprenant.id = :apprenantId AND p.estComplete = true")
    List<Progression> findCompletedByApprenantId(@Param("apprenantId") Long apprenantId);

    @Query("SELECT p FROM Progression p WHERE p.apprenant.id = :apprenantId AND p.estComplete = false")
    List<Progression> findInProgressByApprenantId(@Param("apprenantId") Long apprenantId);

    @Query("SELECT p FROM Progression p WHERE p.apprenant.id = :apprenantId AND p.contenuItem.section.cours.id = :coursId")
    List<Progression> findByApprenantIdAndCoursId(@Param("apprenantId") Long apprenantId, @Param("coursId") Long coursId);

    @Query("SELECT COUNT(p) FROM Progression p WHERE p.apprenant.id = :apprenantId AND p.contenuItem.section.cours.id = :coursId AND p.estComplete = true")
    long countCompletedByApprenantIdAndCoursId(@Param("apprenantId") Long apprenantId, @Param("coursId") Long coursId);

    // Compter les contenus complétés par un étudiant pour un cours
    @Query("SELECT COUNT(p) FROM Progression p WHERE p.apprenant.id = :apprenantId AND p.contenuItem.section.cours.id = :coursId AND p.estComplete = true")
    Long countCompletedByApprenantAndCours(@Param("apprenantId") Long apprenantId, @Param("coursId") Long coursId);

    // Statistiques
    @Query("SELECT AVG(p.tempsPasse) FROM Progression p WHERE p.contenuItem.id = :contenuItemId AND p.estComplete = true")
    Optional<Double> getTempsMoyenCompletion(@Param("contenuItemId") Long contenuItemId);

    @Query("SELECT COUNT(p) FROM Progression p WHERE p.contenuItem.id = :contenuItemId AND p.estComplete = true")
    long countCompletionsByContenuItemId(@Param("contenuItemId") Long contenuItemId);

    // Progression par cours et étudiant
    @Query("SELECT COALESCE(SUM(CASE WHEN p.estComplete = true THEN 1 ELSE 0 END) * 100 / COUNT(p), 0) FROM Progression p WHERE p.contenuItem.section.cours.id = :coursId AND p.apprenant.id = :etudiantId")
    int getProgressionByCoursAndEtudiant(@Param("coursId") Long coursId, @Param("etudiantId") Long etudiantId);

    @Query("SELECT COUNT(p) > 0 FROM Progression p WHERE p.contenuItem.chapitre.id = :chapitreId AND p.apprenant.id = :etudiantId AND p.estComplete = true AND p.contenuItem.chapitre IS NOT NULL")
    boolean isChapitreComplete(@Param("chapitreId") Long chapitreId, @Param("etudiantId") Long etudiantId);

    @Query("SELECT COUNT(p) > 0 FROM Progression p WHERE p.contenuItem.id = :contenuId AND p.apprenant.id = :etudiantId AND p.estComplete = true")
    boolean isContenuComplete(@Param("contenuId") Long contenuId, @Param("etudiantId") Long etudiantId);

    // ==================== MISE À JOUR ====================

    @Transactional
    @Modifying
    @Query("UPDATE Progression p SET p.estComplete = true, p.dateCompletion = CURRENT_TIMESTAMP WHERE p.id = :id")
    void markAsCompleted(@Param("id") Long id);

    @Transactional
    @Modifying
    @Query("UPDATE Progression p SET p.tempsPasse = :tempsPasse, p.dernierePosition = :position, p.dateDerniereActivite = CURRENT_TIMESTAMP WHERE p.id = :id")
    void updateProgress(@Param("id") Long id, @Param("tempsPasse") Integer tempsPasse, @Param("position") Integer position);

    // ==================== SUPPRESSION ====================

    @Transactional
    @Modifying
    @Query("DELETE FROM Progression p WHERE p.contenuItem.section.cours.id = :coursId")
    void deleteByCoursId(@Param("coursId") Long coursId);

    @Transactional
    @Modifying
    @Query("DELETE FROM Progression p WHERE p.contenuItem.id = :contenuItemId")
    void deleteByContenuItemId(@Param("contenuItemId") Long contenuItemId);

    @Transactional
    @Modifying
    @Query("DELETE FROM Progression p WHERE p.contenuItem.chapitre.id = :chapitreId")
    void deleteByChapitreId(@Param("chapitreId") Long chapitreId);

    @Transactional
    @Modifying
    @Query("DELETE FROM Progression p WHERE p.apprenant.id = :apprenantId")
    void deleteByApprenantId(@Param("apprenantId") Long apprenantId);
}