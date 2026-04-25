package com.plateforme_etudiant.demo.service;

import com.plateforme_etudiant.demo.model.*;
import com.plateforme_etudiant.demo.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class QuizService {

    private final QuizRepository quizRepository;
    private final QuestionRepository questionRepository;
    private final ReponseRepository reponseRepository;
    private final ResultatQuizRepository resultatQuizRepository;
    private final CoursRepository coursRepository;
    private final UtilisateurRepository utilisateurRepository;

    public QuizService(QuizRepository quizRepository, QuestionRepository questionRepository,
                       ReponseRepository reponseRepository, ResultatQuizRepository resultatQuizRepository,
                       CoursRepository coursRepository, UtilisateurRepository utilisateurRepository) {
        this.quizRepository = quizRepository;
        this.questionRepository = questionRepository;
        this.reponseRepository = reponseRepository;
        this.resultatQuizRepository = resultatQuizRepository;
        this.coursRepository = coursRepository;
        this.utilisateurRepository = utilisateurRepository;
    }

    @Transactional(readOnly = true)
    public List<Quiz> getQuizByCours(Long coursId) {
        return quizRepository.findAllWithQuestionsByCoursId(coursId);
    }

    @Transactional
    public Quiz creerQuiz(Long coursId, Quiz quiz) {
        Cours cours = coursRepository.findById(coursId)
                .orElseThrow(() -> new RuntimeException("Cours introuvable"));
        quiz.setCours(cours);
        return quizRepository.save(quiz);
    }

    @Transactional
    public Question ajouterQuestion(Long quizId, Question question) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz introuvable"));
        question.setQuiz(quiz);
        return questionRepository.save(question);
    }

    @Transactional
    public Reponse ajouterReponse(Long questionId, Reponse reponse) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("Question introuvable"));
        reponse.setQuestion(question);
        return reponseRepository.save(reponse);
    }

    @Transactional(readOnly = true)
    public Quiz getQuizById(Long id) {
        return quizRepository.findDetailsById(id)
                .orElseThrow(() -> new RuntimeException("Quiz introuvable"));
    }

    @Transactional
    public ResultatQuiz soumettreQuiz(Long quizId, Long utilisateurId, Map<Long, Long> reponsesSoumises) {
        Quiz quiz = getQuizById(quizId);
        Utilisateur etudiant = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        int totalQuestions = quiz.getQuestions().size();
        int reponsesCorrectes = 0;

        for (Question question : quiz.getQuestions()) {
            Long reponseIdSoumise = reponsesSoumises.get(question.getId());
            if (reponseIdSoumise != null) {
                Optional<Reponse> reponseOpt = reponseRepository.findById(reponseIdSoumise);
                if (reponseOpt.isPresent() && reponseOpt.get().getEstCorrecte()) {
                    reponsesCorrectes++;
                }
            }
        }

        double score = totalQuestions > 0 ? ((double) reponsesCorrectes / totalQuestions) * 100 : 0;

        ResultatQuiz resultat = new ResultatQuiz();
        resultat.setQuiz(quiz);
        resultat.setEtudiant(etudiant);
        resultat.setScore(score);

        return resultatQuizRepository.save(resultat);
    }
}
