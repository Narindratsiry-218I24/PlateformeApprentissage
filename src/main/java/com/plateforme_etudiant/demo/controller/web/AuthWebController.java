package com.plateforme_etudiant.demo.controller.web;

import com.plateforme_etudiant.demo.model.Professeur;
import com.plateforme_etudiant.demo.model.Utilisateur;
import com.plateforme_etudiant.demo.model.enums.Role;
import com.plateforme_etudiant.demo.repository.ProfesseurRepository;
import com.plateforme_etudiant.demo.repository.UtilisateurRepository;
import com.plateforme_etudiant.demo.service.MailService;
import com.plateforme_etudiant.demo.service.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Collections;
import java.util.Map;

@Controller
public class AuthWebController {

    private static final Logger log = LoggerFactory.getLogger(AuthWebController.class);

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private ProfesseurRepository professeurRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MailService mailService;

    @Autowired
    private TokenService tokenService;

    private final SecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();

    @GetMapping("/login")
    public String loginPage(@RequestParam(required = false) String error,
                            @RequestParam(required = false) String logout,
                            @RequestParam(required = false) String success,
                            Model model) {
        if (error != null) model.addAttribute("error", "Email ou mot de passe incorrect");
        if (logout != null) model.addAttribute("success", "Vous avez été déconnecté");
        if (success != null) model.addAttribute("success", success);
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String email,
                        @RequestParam String password,
                        HttpServletRequest request,
                        HttpServletResponse response,
                        HttpSession session,
                        Model model) {
        try {
            Utilisateur utilisateur = utilisateurRepository.findByEmail(email).orElse(null);

            if (utilisateur == null || !passwordEncoder.matches(password, utilisateur.getMotDePasse())) {
                model.addAttribute("error", "Email ou mot de passe incorrect");
                return "login";
            }

            if (!utilisateur.getActif()) {
                model.addAttribute("error", "Votre compte est désactivé");
                return "login";
            }

            org.springframework.security.core.userdetails.User securityUser =
                    new org.springframework.security.core.userdetails.User(
                            utilisateur.getEmail(),
                            utilisateur.getMotDePasse(),
                            Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + utilisateur.getRole().name()))
                    );

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(securityUser, null, securityUser.getAuthorities());
            
            request.changeSessionId();
            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authentication);
            SecurityContextHolder.setContext(context);
            securityContextRepository.saveContext(context, request, response);

            log.info("✅ Authentification réussie pour {}, Rôle: {}", email, utilisateur.getRole());
            
            String loginToken = tokenService.generateToken();

            session.setAttribute("userId", utilisateur.getId());
            session.setAttribute("utilisateur", utilisateur); // Add full user object to session for admin check
            session.setAttribute("userEmail", utilisateur.getEmail());
            session.setAttribute("userNom", utilisateur.getPrenom() + " " + utilisateur.getNom());
            session.setAttribute("userRole", utilisateur.getRole().toString());
            session.setAttribute("loginToken", loginToken);

            if (utilisateur.getRole() == Role.PROFESSEUR) {
                Professeur professeur = professeurRepository.findByUtilisateur(utilisateur).orElse(null);
                if (professeur == null) {
                    model.addAttribute("error", "Compte professeur non configuré");
                    return "login";
                }
                session.setAttribute("professeurId", professeur.getId());
                return "redirect:/professeur/dashboard";

            } else if (utilisateur.getRole() == Role.APPRENANT) {
                return "redirect:/etudiant/dashboard";

            } else if (utilisateur.getRole() == Role.ADMINISTRATEUR) {
                return "redirect:/admin/dashboard";
            }

            return "redirect:/";
        } catch (Exception e) {
            model.addAttribute("error", "Erreur technique: " + e.getMessage());
            return "login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        SecurityContextHolder.clearContext();
        return "redirect:/login?logout=true";
    }

    @GetMapping("/forgot-password")
    public String forgotPasswordPage() {
        return "forgot-password";
    }

    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam String email, Model model) {
        if (!utilisateurRepository.existsByEmail(email)) {
            model.addAttribute("success", "Si cette adresse email existe, vous recevrez un lien de réinitialisation.");
            return "forgot-password";
        }
        try {
            String resetToken = tokenService.generateToken();
            tokenService.storeToken(resetToken, email, 1);
            mailService.sendPasswordResetEmail(email, resetToken);
            model.addAttribute("success", "Un email de réinitialisation a été envoyé.");
        } catch (Exception e) {
            model.addAttribute("error", "Une erreur est survenue.");
        }
        return "forgot-password";
    }

    @GetMapping("/reset-password")
    public String resetPasswordPage(@RequestParam String token, Model model) {
        String email = tokenService.validateToken(token);
        if (email == null) {
            model.addAttribute("error", "Ce lien est invalide ou a expiré.");
            return "login";
        }
        model.addAttribute("token", token);
        return "reset-password";
    }

    @PostMapping("/reset-password")
    public String processResetPassword(@RequestParam String token,
                                       @RequestParam String password,
                                       @RequestParam String confirmPassword,
                                       Model model) {
        if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "Les mots de passe ne correspondent pas.");
            model.addAttribute("token", token);
            return "reset-password";
        }
        String email = tokenService.validateToken(token);
        if (email == null) {
            model.addAttribute("error", "Ce lien est invalide ou a expiré.");
            return "login";
        }
        try {
            Utilisateur utilisateur = utilisateurRepository.findByEmail(email).orElse(null);
            if (utilisateur != null) {
                utilisateur.setMotDePasse(passwordEncoder.encode(password));
                utilisateurRepository.save(utilisateur);
                tokenService.removeToken(token);
                return "redirect:/login?success=Mot de passe réinitialisé avec succès !";
            }
        } catch (Exception e) {
            model.addAttribute("error", "Une erreur est survenue.");
        }
        return "reset-password";
    }

    @GetMapping("/api/check-email")
    @ResponseBody
    public Map<String, Boolean> checkEmailExists(@RequestParam String email) {
        return Map.of("exists", utilisateurRepository.existsByEmail(email));
    }
}
