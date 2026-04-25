package com.plateforme_etudiant.demo.controller.web;

import com.plateforme_etudiant.demo.model.*;
import com.plateforme_etudiant.demo.service.QuizService;
import com.plateforme_etudiant.demo.service.UtilisateurService;
import com.plateforme_etudiant.demo.service.cours.CourseReadService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class QuizWebController {

    private final QuizService quizService;
    private final CourseReadService courseReadService;
    private final UtilisateurService utilisateurService;

    public QuizWebController(QuizService quizService, CourseReadService courseReadService, UtilisateurService utilisateurService) {
        this.quizService = quizService;
        this.courseReadService = courseReadService;
        this.utilisateurService = utilisateurService;
    }

    // --- Côté Professeur ---

    @GetMapping("/professeur/cours/{coursId}/quiz")
    public String gererQuiz(@PathVariable Long coursId, Model model, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";
        model.addAttribute("cours", courseReadService.getCoursParId(coursId));
        model.addAttribute("quizzes", quizService.getQuizByCours(coursId));
        model.addAttribute("nouveauQuiz", new Quiz());
        return "professeur/cours/quiz-gerer";
    }

    @PostMapping("/professeur/cours/{coursId}/quiz/ajouter")
    public String ajouterQuiz(@PathVariable Long coursId, @ModelAttribute Quiz quiz, RedirectAttributes redirectAttributes) {
        quizService.creerQuiz(coursId, quiz);
        redirectAttributes.addFlashAttribute("success", "Quiz créé avec succès !");
        return "redirect:/professeur/cours/" + coursId + "/quiz";
    }

    @GetMapping("/professeur/quiz/{quizId}")
    public String detailQuiz(@PathVariable Long quizId, Model model) {
        Quiz quiz = quizService.getQuizById(quizId);
        model.addAttribute("quiz", quiz);
        model.addAttribute("nouvelleQuestion", new Question());
        model.addAttribute("nouvelleReponse", new Reponse());
        return "professeur/cours/quiz-detail";
    }

    @PostMapping("/professeur/quiz/{quizId}/question/ajouter")
    public String ajouterQuestion(@PathVariable Long quizId, @ModelAttribute Question question, RedirectAttributes redirectAttributes) {
        quizService.ajouterQuestion(quizId, question);
        redirectAttributes.addFlashAttribute("success", "Question ajoutée avec succès !");
        return "redirect:/professeur/quiz/" + quizId;
    }

    @PostMapping("/professeur/question/{questionId}/reponse/ajouter")
    public String ajouterReponse(@PathVariable Long questionId, @ModelAttribute Reponse reponse, 
                                 @RequestParam Long quizId, RedirectAttributes redirectAttributes) {
        quizService.ajouterReponse(questionId, reponse);
        redirectAttributes.addFlashAttribute("success", "Réponse ajoutée avec succès !");
        return "redirect:/professeur/quiz/" + quizId;
    }

    // --- Côté Étudiant ---

    @GetMapping("/etudiant/cours/{coursId}/quiz/{quizId}")
    public String passerQuiz(@PathVariable Long coursId, @PathVariable Long quizId, Model model) {
        Quiz quiz = quizService.getQuizById(quizId);
        model.addAttribute("quiz", quiz);
        model.addAttribute("cours", courseReadService.getCoursParId(coursId));
        return "Etudiant/quiz-passer";
    }

    @PostMapping("/etudiant/cours/{coursId}/quiz/{quizId}/soumettre")
    public String soumettreQuiz(@PathVariable Long coursId, @PathVariable Long quizId, 
                                @RequestParam Map<String, String> params, 
                                HttpSession session, RedirectAttributes redirectAttributes) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";
        Utilisateur utilisateur = utilisateurService.findById(userId);

        Map<Long, Long> reponsesSoumises = new HashMap<>();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (entry.getKey().startsWith("question_")) {
                Long questionId = Long.parseLong(entry.getKey().replace("question_", ""));
                Long reponseId = Long.parseLong(entry.getValue());
                reponsesSoumises.put(questionId, reponseId);
            }
        }

        ResultatQuiz resultat = quizService.soumettreQuiz(quizId, utilisateur.getId(), reponsesSoumises);
        redirectAttributes.addFlashAttribute("success", "Quiz terminé ! Votre score est de " + String.format("%.2f", resultat.getScore()) + "%");
        return "redirect:/etudiant/cours/" + coursId + "/visionner";
    }
}
