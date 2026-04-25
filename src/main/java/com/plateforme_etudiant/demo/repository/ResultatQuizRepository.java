package com.plateforme_etudiant.demo.repository;

import com.plateforme_etudiant.demo.model.ResultatQuiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResultatQuizRepository extends JpaRepository<ResultatQuiz, Long> {
    List<ResultatQuiz> findByEtudiantId(Long etudiantId);
    List<ResultatQuiz> findByQuizId(Long quizId);
    Optional<ResultatQuiz> findByEtudiantIdAndQuizId(Long etudiantId, Long quizId);
}
