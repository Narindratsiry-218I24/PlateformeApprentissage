package com.plateforme_etudiant.demo.repository;

import com.plateforme_etudiant.demo.model.ResultatQuiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import java.util.Optional;

@Repository
public interface ResultatQuizRepository extends JpaRepository<ResultatQuiz, Long> {
    List<ResultatQuiz> findByEtudiantId(Long etudiantId);
    List<ResultatQuiz> findByQuizId(Long quizId);
    List<ResultatQuiz> findByEtudiantIdAndQuizIdOrderByDatePassageDesc(Long etudiantId, Long quizId);
    Optional<ResultatQuiz> findFirstByEtudiantIdAndQuizIdOrderByDatePassageDesc(Long etudiantId, Long quizId);

    @Transactional
    @Modifying
    @Query("DELETE FROM ResultatQuiz r WHERE r.etudiant.id = :etudiantId AND r.quiz.id = :quizId AND r.id <> :keepId")
    void deleteDuplicatesForPair(@Param("etudiantId") Long etudiantId, @Param("quizId") Long quizId, @Param("keepId") Long keepId);
}
