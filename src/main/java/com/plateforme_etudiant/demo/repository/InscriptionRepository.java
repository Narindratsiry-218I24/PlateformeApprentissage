package com.plateforme_etudiant.demo.repository;

import com.plateforme_etudiant.demo.model.Inscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface InscriptionRepository extends JpaRepository<Inscription, Long> {

    // Vérifier si un étudiant est inscrit à un cours
    boolean existsByApprenantIdAndCoursId(Long apprenantId, Long coursId);

    // Supprimer une inscription
    void deleteByApprenantIdAndCoursId(Long apprenantId, Long coursId);

    // Récupérer les inscriptions d'un étudiant avec les cours
    @Query("SELECT i FROM Inscription i JOIN FETCH i.cours WHERE i.apprenant.id = :apprenantId")
    List<Inscription> findByApprenantIdWithCours(@Param("apprenantId") Long apprenantId);

    // Récupérer les IDs des cours auxquels un étudiant est inscrit
    @Query("SELECT i.cours.id FROM Inscription i WHERE i.apprenant.id = :apprenantId")
    List<Long> findCoursIdsByApprenantId(@Param("apprenantId") Long apprenantId);

    Optional<Inscription> findByApprenantIdAndCoursId(Long apprenantId, Long coursId);

    // Compter le nombre d'inscriptions d'un étudiant
    @Query("SELECT COUNT(i) FROM Inscription i WHERE i.apprenant.id = :apprenantId")
    long countByApprenantId(@Param("apprenantId") Long apprenantId);

    // Récupérer les inscriptions pour un cours spécifique avec les apprenants
    @Query("SELECT i FROM Inscription i JOIN FETCH i.apprenant WHERE i.cours.id = :coursId")
    List<Inscription> findByCoursIdWithApprenant(@Param("coursId") Long coursId);

    // Récupérer les inscriptions d'un étudiant pour les cours d'un professeur
    @Query("SELECT i FROM Inscription i JOIN FETCH i.cours c WHERE i.apprenant.id = :apprenantId AND c.professeur.id = :professeurId")
    List<Inscription> findByApprenantIdAndProfesseurId(@Param("apprenantId") Long apprenantId, @Param("professeurId") Long professeurId);
}
