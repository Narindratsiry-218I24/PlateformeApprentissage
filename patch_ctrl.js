const fs = require('fs');

let code = fs.readFileSync('src/main/java/com/plateforme_etudiant/demo/controller/web/QuizWebController.java', 'utf8');

const imports = `import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ResponseBody;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
`;

code = code.replace(/import org\.springframework\.web\.bind\.annotation\.\*;/g, "import org.springframework.web.bind.annotation.*;\n" + imports);

const newEndpoints = `
    @GetMapping("/api/quiz/{id}/certificat/telecharger")
    @ResponseBody
    public ResponseEntity<byte[]> telechargerCertificat(@PathVariable Long id, @RequestParam String email) {
        Utilisateur etudiant = utilisateurService.findByEmail(email).orElse(null);
        if (etudiant == null) return ResponseEntity.badRequest().build();
        
        ResultatQuiz resultat = quizService.getResultatQuizRepository().findFirstByEtudiantIdAndQuizIdOrderByDatePassageDesc(etudiant.getId(), id).orElse(null);
        if (resultat == null || resultat.getScore() < MIN_SCORE_CERTIFICAT) {
            return ResponseEntity.badRequest().build();
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
        
        Utilisateur etudiant = utilisateurService.findByEmail(email).orElse(null);
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
`;

code = code.replace(/}\s*$/g, newEndpoints + "\n}");

fs.writeFileSync('src/main/java/com/plateforme_etudiant/demo/controller/web/QuizWebController.java', code);
console.log("Endpoints added.");
