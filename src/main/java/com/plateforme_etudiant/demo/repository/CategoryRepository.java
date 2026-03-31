package com.plateforme_etudiant.demo.repository;

import com.plateforme_etudiant.demo.model.Categorie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Categorie, Long> {

    Optional<Categorie> findBySlug(String slug);

    List<Categorie> findByParentIsNull();

    List<Categorie> findByActiveTrue();

    @Query("SELECT c FROM Categorie c WHERE c.parent.id = :parentId")
    List<Categorie> findSousCategoriesByParentId(@Param("parentId") Long parentId);

    boolean existsBySlug(String slug);
}