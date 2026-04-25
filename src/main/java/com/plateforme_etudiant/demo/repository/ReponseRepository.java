package com.plateforme_etudiant.demo.repository;

import com.plateforme_etudiant.demo.model.Reponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReponseRepository extends JpaRepository<Reponse, Long> {
    List<Reponse> findByQuestionId(Long questionId);
}
