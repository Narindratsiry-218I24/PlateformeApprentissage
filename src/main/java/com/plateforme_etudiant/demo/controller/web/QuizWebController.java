package com.plateforme_etudiant.demo.controller.web;

import com.plateforme_etudiant.demo.model.Question;
import com.plateforme_etudiant.demo.model.Quiz;
import com.plateforme_etudiant.demo.model.Reponse;
import com.plateforme_etudiant.demo.model.ResultatQuiz;
import com.plateforme_etudiant.demo.model.Utilisateur;
import com.plateforme_etudiant.demo.service.MailService;
import com.plateforme_etudiant.demo.service.QuizService;
import com.plateforme_etudiant.demo.service.UtilisateurService;
import com.plateforme_etudiant.demo.service.CertificatService;
import com.plateforme_etudiant.demo.service.cours.CourseReadService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

@Controller
@RequestMapping
public class QuizWebController {

    private static final double MIN_SCORE_CERTIFICAT = 100.0;
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");

    private final QuizService quizService;
    private final CourseReadService courseReadService;
    private final UtilisateurService utilisateurService;
    private final MailService mailService;
    private final CertificatService certificatService;

    @Value("${app.base-url:}")
    private String appBaseUrl;

    public QuizWebController(QuizService quizService,
                             CourseReadService courseReadService,
                             UtilisateurService utilisateurService,
                             MailService mailService,
                             CertificatService certificatService) {
        this.quizService = quizService;
        this.courseReadService = courseReadService;
        this.utilisateurService = utilisateurService;
        this.mailService = mailService;
        this.certificatService = certificatService;
    }

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
    public String ajouterQuiz(@PathVariable Long coursId,
                              @ModelAttribute Quiz quiz,
                              HttpSession session,
                              RedirectAttributes redirectAttributes) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";
        quizService.creerQuiz(coursId, quiz);
        redirectAttributes.addFlashAttribute("success", "Quiz cree avec succes.");
        return "redirect:/professeur/cours/" + coursId + "/quiz";
    }

    @GetMapping("/professeur/quiz/{quizId}")
    public String detailQuiz(@PathVariable Long quizId, Model model, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";
        Quiz quiz = quizService.getQuizById(quizId);
        model.addAttribute("quiz", quiz);
        model.addAttribute("nouvelleQuestion", new Question());
        model.addAttribute("nouvelleReponse", new Reponse());
        return "professeur/cours/quiz-detail";
    }

    @PostMapping("/professeur/quiz/{quizId}/question/ajouter")
    public String ajouterQuestion(@PathVariable Long quizId,
                                  @ModelAttribute Question question,
                                  HttpSession session,
                                  RedirectAttributes redirectAttributes) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";
        quizService.ajouterQuestion(quizId, question);
        redirectAttributes.addFlashAttribute("success", "Question ajoutee avec succes.");
        return "redirect:/professeur/quiz/" + quizId;
    }

    @PostMapping("/professeur/question/{questionId}/reponse/ajouter")
    public String ajouterReponse(@PathVariable Long questionId,
                                 @ModelAttribute Reponse reponse,
                                 @RequestParam Long quizId,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";
        quizService.ajouterReponse(questionId, reponse);
        redirectAttributes.addFlashAttribute("success", "Reponse ajoutee avec succes.");
        return "redirect:/professeur/quiz/" + quizId;
    }

    @GetMapping("/etudiant/cours/{coursId}/quiz/{quizId}")
    public String passerQuiz(@PathVariable Long coursId, @PathVariable Long quizId, Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";

        Quiz quiz = quizService.getQuizById(quizId);

        // Vérification du délai de 24h avant de permettre l'accès au quiz
        java.util.Optional<ResultatQuiz> derniereTentativeOpt = quizService.getResultatQuizRepository().findFirstByEtudiantIdAndQuizIdOrderByDatePassageDesc(userId, quizId);
        if (derniereTentativeOpt.isPresent()) {
            ResultatQuiz derniereTentative = derniereTentativeOpt.get();
            if (derniereTentative.getScore() != null && derniereTentative.getScore() < 100.0) {
                java.time.LocalDateTime dateProchaineTentative = derniereTentative.getDatePassage().plusHours(24);
                if (java.time.LocalDateTime.now().isBefore(dateProchaineTentative)) {
                    redirectAttributes.addFlashAttribute("error", "Vous avez échoué à la dernière tentative. Vous devez attendre jusqu'au " + dateProchaineTentative.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy à HH:mm")) + " avant de pouvoir retenter.");
                    return "redirect:/etudiant/cours/" + coursId + "/visionner";
                }
            } else if (derniereTentative.getScore() != null && derniereTentative.getScore() >= 100.0) {
                // Déjà validé, on redirige vers le certificat / résultat
                return "redirect:/etudiant/cours/" + coursId + "/quiz/" + quizId + "/resultat/" + derniereTentative.getId();
            }
        }

        model.addAttribute("quiz", quiz);
        model.addAttribute("cours", courseReadService.getCoursParId(coursId));
        return "etudiant/quiz-passer";
    }

    @PostMapping("/etudiant/cours/{coursId}/quiz/{quizId}/soumettre")
    public String soumettreQuiz(@PathVariable Long coursId,
                                @PathVariable Long quizId,
                                @RequestParam Map<String, String> params,
                                HttpServletRequest request,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";

        Utilisateur utilisateur = utilisateurService.findById(userId);
        Quiz quiz = quizService.getQuizById(quizId);
        Map<Long, Long> reponsesSoumises = new HashMap<>();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (entry.getKey().startsWith("question_")) {
                Long questionId = Long.parseLong(entry.getKey().replace("question_", ""));
                Long reponseId = Long.parseLong(entry.getValue());
                reponsesSoumises.put(questionId, reponseId);
            }
        }

        ResultatQuiz resultat;
        try {
            resultat = quizService.soumettreQuiz(quizId, utilisateur.getId(), reponsesSoumises);
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/etudiant/cours/" + coursId + "/visionner";
        }
        
        double score = resultat.getScore() != null ? resultat.getScore() : 0.0;
        int totalQuestions = quiz.getQuestions() != null ? quiz.getQuestions().size() : 0;
        int correctes = (int) Math.round((score / 100.0) * totalQuestions);
        List<Map<String, Object>> details = buildDetails(quiz, reponsesSoumises);

        boolean certificatDisponible = score >= MIN_SCORE_CERTIFICAT;
        String certificatPath = certificatDisponible ? "/etudiant/certificat/" + resultat.getId() + "/telecharger" : null;
        if (certificatDisponible) {
            String certificatUrl = resolveBaseUrl(request) + certificatPath;
            String coursTitre = resultat.getQuiz() != null && resultat.getQuiz().getCours() != null
                    ? resultat.getQuiz().getCours().getTitre()
                    : "Cours";
            String quizTitre = resultat.getQuiz() != null ? resultat.getQuiz().getTitre() : "Quiz";
            byte[] pdfBytes = certificatService.genererCertificatPDF(resultat);
            mailService.sendQuizCertificateEmail(
                    utilisateur.getEmail(),
                    utilisateur.getNomComplet(),
                    coursTitre,
                    quizTitre,
                    score,
                    certificatUrl,
                    pdfBytes
            );
        }

        redirectAttributes.addFlashAttribute("quiz", quiz);
        redirectAttributes.addFlashAttribute("cours", courseReadService.getCoursParId(coursId));
        redirectAttributes.addFlashAttribute("score", score);
        redirectAttributes.addFlashAttribute("correctes", correctes);
        redirectAttributes.addFlashAttribute("totalQuestions", totalQuestions);
        redirectAttributes.addFlashAttribute("details", details);
        redirectAttributes.addFlashAttribute("certificatDisponible", certificatDisponible);
        redirectAttributes.addFlashAttribute("certificatUrl", certificatPath);

        return "redirect:/etudiant/cours/" + coursId + "/quiz/" + quizId + "/resultat/" + resultat.getId();
    }

    @GetMapping("/etudiant/cours/{coursId}/quiz/{quizId}/resultat/{resultatId}")
    public String resultatQuiz(@PathVariable Long coursId,
                               @PathVariable Long quizId,
                               @PathVariable Long resultatId,
                               Model model,
                               HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";

        ResultatQuiz resultat = quizService.getResultatById(resultatId);
        if (resultat.getEtudiant() == null || !userId.equals(resultat.getEtudiant().getId())) {
            return "redirect:/etudiant/cours/" + coursId + "/visionner";
        }

        if (!model.containsAttribute("quiz")) {
            model.addAttribute("quiz", quizService.getQuizById(quizId));
        }
        if (!model.containsAttribute("cours")) {
            model.addAttribute("cours", courseReadService.getCoursParId(coursId));
        }
        if (!model.containsAttribute("score")) {
            model.addAttribute("score", resultat.getScore() != null ? resultat.getScore() : 0.0);
        }
        if (!model.containsAttribute("details")) {
            model.addAttribute("details", new ArrayList<>());
        }
        if (!model.containsAttribute("certificatDisponible")) {
            model.addAttribute("certificatDisponible", resultat.getScore() != null && resultat.getScore() >= MIN_SCORE_CERTIFICAT);
        }
        if (!model.containsAttribute("certificatUrl")) {
            model.addAttribute("certificatUrl", "/etudiant/certificat/" + resultat.getId() + "/telecharger");
        }
        if (!model.containsAttribute("certificatEmail")) {
            model.addAttribute("certificatEmail", resultat.getEtudiant() != null ? resultat.getEtudiant().getEmail() : "");
        }
        model.addAttribute("resultatId", resultat.getId());

        if (!model.containsAttribute("dateProchaine")) {
            if (resultat.getScore() != null && resultat.getScore() < 100.0) {
                model.addAttribute("dateProchaine", resultat.getDatePassage().plusHours(24));
            } else {
                model.addAttribute("dateProchaine", null);
            }
        }
        
        return "etudiant/quiz-resultat";
    }

    @PostMapping("/etudiant/cours/{coursId}/quiz/{quizId}/resultat/{resultatId}/envoyer-certificat")
    public String envoyerCertificatParEmail(@PathVariable Long coursId,
                                            @PathVariable Long quizId,
                                            @PathVariable Long resultatId,
                                            @RequestParam(required = false) String email,
                                            HttpServletRequest request,
                                            HttpSession session,
                                            RedirectAttributes redirectAttributes) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }

        ResultatQuiz resultat = quizService.getResultatById(resultatId);
        if (resultat.getEtudiant() == null || !userId.equals(resultat.getEtudiant().getId())) {
            return "redirect:/etudiant/cours/" + coursId + "/visionner";
        }
        if (resultat.getScore() == null || resultat.getScore() < MIN_SCORE_CERTIFICAT) {
            redirectAttributes.addFlashAttribute("certificatError", "Le certificat est disponible uniquement avec 100%.");
            return "redirect:/etudiant/cours/" + coursId + "/quiz/" + quizId + "/resultat/" + resultatId;
        }

        String destination = (email == null || email.isBlank()) ? resultat.getEtudiant().getEmail() : email.trim().toLowerCase();
        if (!EMAIL_PATTERN.matcher(destination).matches()) {
            redirectAttributes.addFlashAttribute("certificatError", "Veuillez saisir une adresse email valide.");
            redirectAttributes.addFlashAttribute("certificatEmail", destination);
            return "redirect:/etudiant/cours/" + coursId + "/quiz/" + quizId + "/resultat/" + resultatId;
        }

        String certificatUrl = resolveBaseUrl(request) + "/etudiant/certificat/" + resultat.getId() + "/telecharger";
        String coursTitre = resultat.getQuiz() != null && resultat.getQuiz().getCours() != null
                ? resultat.getQuiz().getCours().getTitre()
                : "Cours";
        String quizTitre = resultat.getQuiz() != null ? resultat.getQuiz().getTitre() : "Quiz";

        byte[] pdfBytes = certificatService.genererCertificatPDF(resultat);
        boolean sent = mailService.sendQuizCertificateEmail(
                destination,
                resultat.getEtudiant().getNomComplet(),
                coursTitre,
                quizTitre,
                resultat.getScore(),
                certificatUrl,
                pdfBytes
        );

        if (sent) {
            redirectAttributes.addFlashAttribute("certificatSuccess", "Certificat envoye a " + destination + ".");
        } else {
            redirectAttributes.addFlashAttribute("certificatError", "Echec de l'envoi email, veuillez reessayer.");
        }
        redirectAttributes.addFlashAttribute("certificatEmail", destination);
        return "redirect:/etudiant/cours/" + coursId + "/quiz/" + quizId + "/resultat/" + resultatId;
    }

    @GetMapping("/etudiant/certificat/{resultatId}/telecharger")
    public ResponseEntity<byte[]> telechargerCertificat(@PathVariable Long resultatId, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(401).build();
        }

        ResultatQuiz resultat = quizService.getResultatById(resultatId);
        if (resultat == null || resultat.getEtudiant() == null || !userId.equals(resultat.getEtudiant().getId())) {
            return ResponseEntity.status(403).build();
        }
        if (resultat.getScore() == null || resultat.getScore() < MIN_SCORE_CERTIFICAT) {
            return ResponseEntity.badRequest().build();
        }

        byte[] pdfBytes = certificatService.genererCertificatPDF(resultat);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "certificat-erudition-" + resultatId + ".pdf");
        return ResponseEntity.ok().headers(headers).body(pdfBytes);
    }

    private String resolveBaseUrl(HttpServletRequest request) {
        if (appBaseUrl != null && !appBaseUrl.isBlank()) {
            return appBaseUrl.endsWith("/") ? appBaseUrl.substring(0, appBaseUrl.length() - 1) : appBaseUrl;
        }
        String scheme = request.getScheme();
        String host = request.getServerName();
        int port = request.getServerPort();
        boolean defaultPort = ("http".equalsIgnoreCase(scheme) && port == 80)
                || ("https".equalsIgnoreCase(scheme) && port == 443);
        return defaultPort ? scheme + "://" + host : scheme + "://" + host + ":" + port;
    }

    private List<Map<String, Object>> buildDetails(Quiz quiz, Map<Long, Long> reponsesSoumises) {
        List<Map<String, Object>> details = new ArrayList<>();
        if (quiz.getQuestions() == null) {
            return details;
        }

        for (Question question : quiz.getQuestions()) {
            Long chosenId = reponsesSoumises.get(question.getId());
            Optional<Reponse> chosen = question.getReponses() == null ? Optional.empty() :
                    question.getReponses().stream().filter(r -> r.getId().equals(chosenId)).findFirst();
            Optional<Reponse> correct = question.getReponses() == null ? Optional.empty() :
                    question.getReponses().stream().filter(Reponse::getEstCorrecte).findFirst();

            Map<String, Object> row = new HashMap<>();
            row.put("question", question.getTexteQuestion());
            row.put("choisie", chosen.map(Reponse::getTexteReponse).orElse("Aucune reponse"));
            row.put("correcte", correct.map(Reponse::getTexteReponse).orElse("-"));
            row.put("estCorrecte", chosen.isPresent() && Boolean.TRUE.equals(chosen.get().getEstCorrecte()));
            details.add(row);
        }
        return details;
    }

    @GetMapping("/api/quiz/{id}/certificat/telecharger")
    @ResponseBody
    public ResponseEntity<?> telechargerCertificat(@PathVariable Long id, @RequestParam String email) {
        Utilisateur etudiant;
        try {
            etudiant = utilisateurService.findByEmail(email);
        } catch (RuntimeException ex) {
            etudiant = null;
        }
        if (etudiant == null) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Utilisateur non trouvé avec cet email");
            return ResponseEntity.badRequest().body(error);
        }

        ResultatQuiz resultat = quizService.getResultatQuizRepository().findFirstByEtudiantIdAndQuizIdOrderByDatePassageDesc(etudiant.getId(), id).orElse(null);
        if (resultat == null) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Aucun résultat trouvé pour ce quiz et cet étudiant");
            return ResponseEntity.badRequest().body(error);
        }
        if (resultat.getScore() == null || resultat.getScore() < MIN_SCORE_CERTIFICAT) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Score insuffisant pour obtenir un certificat");
            return ResponseEntity.badRequest().body(error);
        }

        byte[] pdfBytes = certificatService.genererCertificatPDF(resultat);
        String quizTitre = resultat.getQuiz() != null ? resultat.getQuiz().getTitre().replace(" ", "_") : "Quiz";
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "certificat-" + quizTitre + ".pdf");
        
        return ResponseEntity.ok().headers(headers).body(pdfBytes);
    }

    @PostMapping("/api/quiz/{id}/certificat/envoyer")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> envoyerCertificat(@PathVariable Long id, @RequestParam String email) {
        Map<String, Object> response = new HashMap<>();
        
        Utilisateur etudiant;
        try {
            etudiant = utilisateurService.findByEmail(email);
        } catch (RuntimeException ex) {
            etudiant = null;
        }
        if (etudiant == null) {
            response.put("success", false);
            response.put("message", "Utilisateur non trouvé avec cet email.");
            return ResponseEntity.badRequest().body(response);
        }
        
        ResultatQuiz resultat = quizService.getResultatQuizRepository().findFirstByEtudiantIdAndQuizIdOrderByDatePassageDesc(etudiant.getId(), id).orElse(null);
        if (resultat == null || resultat.getScore() < MIN_SCORE_CERTIFICAT) {
            response.put("success", false);
            response.put("message", "Aucun résultat éligible trouvé.");
            return ResponseEntity.badRequest().body(response);
        }

        byte[] pdfBytes = certificatService.genererCertificatPDF(resultat);
        
        String coursTitre = resultat.getQuiz() != null && resultat.getQuiz().getCours() != null ? resultat.getQuiz().getCours().getTitre() : "Cours";
        String quizTitre = resultat.getQuiz() != null ? resultat.getQuiz().getTitre() : "Quiz";
        
        boolean sent = mailService.sendQuizCertificateEmail(
            email,
            etudiant.getNomComplet(),
            coursTitre,
            quizTitre,
            resultat.getScore(),
            "/api/quiz/" + id + "/certificat/telecharger?email=" + email,
            pdfBytes
        );

        if (sent) {
            response.put("success", true);
            response.put("message", "Certificat envoyé.");
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "Erreur d'envoi de l'email.");
            return ResponseEntity.internalServerError().body(response);
        }
    }

}