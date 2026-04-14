package com.plateforme_etudiant.demo.repository;

import com.plateforme_etudiant.demo.model.Utilisateur;
import com.plateforme_etudiant.demo.model.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {

    Optional<Utilisateur> findByEmail(String email);

    Optional<Utilisateur> findByNomUtilisateur(String nomUtilisateur);

    boolean existsByEmail(String email);

    boolean existsByNomUtilisateur(String nomUtilisateur);

    List<Utilisateur> findByRole(Role role);

    List<Utilisateur> findByActifTrue();

    // Recherche par mot-clé dans nom, prénom, email ou nom d'utilisateur
    @Query("SELECT u FROM Utilisateur u WHERE u.nom LIKE %:keyword% OR u.prenom LIKE %:keyword% OR u.email LIKE %:keyword% OR u.nomUtilisateur LIKE %:keyword%")
    List<Utilisateur> searchByKeyword(@Param("keyword") String keyword);
}