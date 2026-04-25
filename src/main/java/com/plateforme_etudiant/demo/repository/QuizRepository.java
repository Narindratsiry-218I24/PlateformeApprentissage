package com.plateforme_etudiant.demo.repository;

import com.plateforme_etudiant.demo.model.Cours;
import com.plateforme_etudiant.demo.model.Quiz;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long> {
    List<Quiz> findByCours(Cours cours);
    List<Quiz> findByCoursId(Long coursId);

    @Query("""
            select distinct q
            from Quiz q
            left join fetch q.questions questions
            where q.cours.id = :coursId
            order by q.dateCreation desc
            """)
    List<Quiz> findAllWithQuestionsByCoursId(@Param("coursId") Long coursId);

    @EntityGraph(attributePaths = {"cours", "questions", "questions.reponses"})
    @Query("select q from Quiz q where q.id = :id")
    Optional<Quiz> findDetailsById(@Param("id") Long id);
}
