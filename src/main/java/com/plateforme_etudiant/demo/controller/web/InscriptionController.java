package com.plateforme_etudiant.demo.controller.web;

import com.plateforme_etudiant.demo.model.Utilisateur;
import com.plateforme_etudiant.demo.model.enums.Role;
import com.plateforme_etudiant.demo.repository.UtilisateurRepository;
import com.plateforme_etudiant.demo.service.MailService;
import com.plateforme_etudiant.demo.service.VerificationCodeService;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;

@Controller
public class InscriptionController {

    private static final Logger log = LoggerFactory.getLogger(InscriptionController.class);

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MailService mailService;

    @Autowired
    private VerificationCodeService verificationCodeService;

    // ==========================================
    // PAGE D'INSCRIPTION - ÉTAPE 1
    // ==========================================

    @GetMapping("/inscription")
    public String inscriptionPage(HttpSession session) {
        nettoyerSession(session);
        return "inscription";
    }

    /**
     * Étape 1 : Nom et Prénom
     */
    @PostMapping("/inscription/etape1")
    public String etape1(@RequestParam String prenom,
                         @RequestParam String nom,
                         HttpSession session,
                         Model model) {

        log.info("=== ÉTAPE 1 : INFOS PERSONNELLES ===");
        log.info("Prénom: {}, Nom: {}", prenom, nom);

        // Validation prénom
        if (prenom == null || prenom.trim().length() < 2) {
            model.addAttribute("error", "Le prénom doit contenir au moins 2 caractères");
            return "inscription";
        }

        // Validation nom
        if (nom == null || nom.trim().length() < 2) {
            model.addAttribute("error", "Le nom doit contenir au moins 2 caractères");
            return "inscription";
        }

        // Stocker en session
        session.setAttribute("inscription_prenom", prenom.trim());
        session.setAttribute("inscription_nom", nom.trim());
        session.setAttribute("inscription_etape", 1);

        return "redirect:/inscription/etape2";
    }

    // ==========================================
    // ÉTAPE 2 : Email, Username, Mot de passe
    // ==========================================

    @GetMapping("/inscription/etape2")
    public String etape2Page(HttpSession session, Model model) {
        Integer etape = (Integer) session.getAttribute("inscription_etape");
        if (etape == null || etape < 1) {
            return "redirect:/inscription";
        }

        model.addAttribute("prenom", session.getAttribute("inscription_prenom"));
        model.addAttribute("nom", session.getAttribute("inscription_nom"));

        return "inscription-etape2";
    }

    /**
     * Étape 2 : Validation email, username, mot de passe
     */
    @PostMapping("/inscription/etape2")
    public String etape2(@RequestParam String email,
                         @RequestParam String nomUtilisateur,
                         @RequestParam String motDePasse,
                         @RequestParam String confirmMotDePasse,
                         HttpSession session,
                         Model model) {

        log.info("=== ÉTAPE 2 : IDENTIFIANTS ===");
        log.info("Email: {}, Username: {}", email, nomUtilisateur);

        // Validation email
        if (email == null || !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            model.addAttribute("error", "Format d'email invalide");
            chargerDonneesEtape1(session, model);
            return "inscription-etape2";
        }

        // Validation nom d'utilisateur
        if (nomUtilisateur == null || nomUtilisateur.trim().length() < 3) {
            model.addAttribute("error", "Le nom d'utilisateur doit contenir au moins 3 caractères");
            chargerDonneesEtape1(session, model);
            return "inscription-etape2";
        }

        // Validation mots de passe
        if (!motDePasse.equals(confirmMotDePasse)) {
            model.addAttribute("error", "Les mots de passe ne correspondent pas");
            chargerDonneesEtape1(session, model);
            return "inscription-etape2";
        }

        if (motDePasse.length() < 6) {
            model.addAttribute("error", "Le mot de passe doit contenir au moins 6 caractères");
            chargerDonneesEtape1(session, model);
            return "inscription-etape2";
        }

        // Vérifier unicité email
        if (utilisateurRepository.existsByEmail(email.trim().toLowerCase())) {
            model.addAttribute("error", "Cet email est déjà utilisé");
            chargerDonneesEtape1(session, model);
            return "inscription-etape2";
        }

        // Vérifier unicité username
        if (utilisateurRepository.existsByNomUtilisateur(nomUtilisateur.trim())) {
            model.addAttribute("error", "Ce nom d'utilisateur est déjà pris");
            chargerDonneesEtape1(session, model);
            return "inscription-etape2";
        }

        // Stocker en session
        session.setAttribute("inscription_email", email.trim().toLowerCase());
        session.setAttribute("inscription_nomUtilisateur", nomUtilisateur.trim());
        session.setAttribute("inscription_motDePasse", motDePasse);
        session.setAttribute("inscription_etape", 2);

        return "redirect:/inscription/etape3";
    }

    // ==========================================
    // ÉTAPE 3 : Vérification email
    // ==========================================

    @GetMapping("/inscription/etape3")
    public String etape3Page(HttpSession session, Model model) {
        Integer etape = (Integer) session.getAttribute("inscription_etape");
        if (etape == null || etape < 2) {
            return "redirect:/inscription";
        }

        String email = (String) session.getAttribute("inscription_email");

        try {
            // Générer et envoyer le code
            String code = verificationCodeService.generateCode();
            verificationCodeService.storeCode(email, code);

            log.info("📧 Code pour {}: {}", email, code);

            boolean sent = mailService.sendVerificationCode(email, code);

            model.addAttribute("emailMasque", masquerEmail(email));
            model.addAttribute("codeEnvoye", sent);

            if (!sent) {
                model.addAttribute("error", "Erreur lors de l'envoi du code.");
            }

        } catch (Exception e) {
            log.error("❌ Erreur envoi code: {}", e.getMessage());
            model.addAttribute("emailMasque", masquerEmail(email));
            model.addAttribute("error", "Erreur: " + e.getMessage());
        }

        return "inscription-etape3";
    }

    /**
     * Étape 3 : Vérification du code et création du compte
     */
    @PostMapping("/inscription/etape3")
    public String etape3(@RequestParam String code,
                         HttpSession session,
                         Model model) {

        String email = (String) session.getAttribute("inscription_email");

        if (email == null) {
            return "redirect:/inscription";
        }

        log.info("=== ÉTAPE 3 : VÉRIFICATION ===");
        log.info("Code saisi: {}", code);

        // Vérifier le code
        boolean codeValide = verificationCodeService.verifyCode(email, code);

        if (!codeValide) {
            model.addAttribute("emailMasque", masquerEmail(email));
            model.addAttribute("error", "Code invalide ou expiré. Veuillez réessayer.");
            model.addAttribute("codeEnvoye", true);
            return "inscription-etape3";
        }

        // ✅ Code valide → Créer le compte étudiant
        try {
            String prenom = (String) session.getAttribute("inscription_prenom");
            String nom = (String) session.getAttribute("inscription_nom");
            String nomUtilisateur = (String) session.getAttribute("inscription_nomUtilisateur");
            String motDePasse = (String) session.getAttribute("inscription_motDePasse");

            // Créer l'utilisateur (toujours APPRENANT)
            Utilisateur utilisateur = new Utilisateur();
            utilisateur.setEmail(email);
            utilisateur.setNomUtilisateur(nomUtilisateur);
            utilisateur.setMotDePasse(passwordEncoder.encode(motDePasse));
            utilisateur.setPrenom(prenom);
            utilisateur.setNom(nom);
            utilisateur.setRole(Role.APPRENANT); // Toujours étudiant
            utilisateur.setActif(true);
            utilisateur.setDateCreation(LocalDateTime.now());

            Utilisateur savedUser = utilisateurRepository.save(utilisateur);
            log.info("✅ Compte étudiant créé ID: {}", savedUser.getId());

            // Envoyer email de bienvenue
            mailService.sendWelcomeEmail(email, prenom);

            // Nettoyer la session
            nettoyerSession(session);

            // Rediriger vers login avec message de succès
            return "redirect:/login?success=Inscription+r%C3%A9ussie+!+Vous+pouvez+maintenant+vous+connecter.";

        } catch (Exception e) {
            log.error("❌ Erreur création compte: {}", e.getMessage(), e);
            model.addAttribute("emailMasque", masquerEmail(email));
            model.addAttribute("error", "Erreur lors de la création du compte: " + e.getMessage());
            return "inscription-etape3";
        }
    }

    // ==========================================
    // RENVOYER LE CODE
    // ==========================================

    @PostMapping("/inscription/renvoyer-code")
    public String renvoyerCode(HttpSession session, Model model) {
        String email = (String) session.getAttribute("inscription_email");

        if (email == null) {
            return "redirect:/inscription";
        }

        String code = verificationCodeService.generateCode();
        verificationCodeService.storeCode(email, code);

        log.info("📧 Nouveau code pour {}: {}", email, code);

        boolean sent = mailService.sendVerificationCode(email, code);

        model.addAttribute("emailMasque", masquerEmail(email));
        model.addAttribute("codeEnvoye", true);

        if (sent) {
            model.addAttribute("success", "✅ Nouveau code envoyé !");
        } else {
            model.addAttribute("error", "❌ Erreur lors de l'envoi.");
        }

        return "inscription-etape3";
    }

    // ==========================================
    // MÉTHODES UTILITAIRES
    // ==========================================

    private String masquerEmail(String email) {
        int atIndex = email.indexOf('@');
        if (atIndex <= 2) return email;
        return email.substring(0, 2) + "***" + email.substring(atIndex);
    }

    private void chargerDonneesEtape1(HttpSession session, Model model) {
        model.addAttribute("prenom", session.getAttribute("inscription_prenom"));
        model.addAttribute("nom", session.getAttribute("inscription_nom"));
    }

    private void nettoyerSession(HttpSession session) {
        session.removeAttribute("inscription_prenom");
        session.removeAttribute("inscription_nom");
        session.removeAttribute("inscription_email");
        session.removeAttribute("inscription_nomUtilisateur");
        session.removeAttribute("inscription_motDePasse");
        session.removeAttribute("inscription_etape");
    }
}