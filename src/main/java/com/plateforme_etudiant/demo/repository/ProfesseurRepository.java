package com.plateforme_etudiant.demo.repository;

import com.plateforme_etudiant.demo.model.Professeur;
import com.plateforme_etudiant.demo.model.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProfesseurRepository extends JpaRepository<Professeur, Long> {

    Optional<Professeur> findByUtilisateur(Utilisateur utilisateur);

    Optional<Professeur> findByUtilisateurId(Long utilisateurId);

    List<Professeur> findByVerifieTrue();

    @Query("SELECT p FROM Professeur p WHERE p.specialite LIKE %:specialite%")
    List<Professeur> findBySpecialiteContaining(@Param("specialite") String specialite);

    @Query("SELECT COUNT(c) FROM Cours c WHERE c.professeur.id = :professeurId")
    long countCoursByProfesseurId(@Param("professeurId") Long professeurId);
}