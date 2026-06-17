package com.plateforme_etudiant.demo.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class MailService {

    private static final Logger log = LoggerFactory.getLogger(MailService.class);

    @Autowired
    private JavaMailSender mailSender;

    public boolean sendHtmlEmail(String to, String subject, String htmlContent) {
        try {
            var message = mailSender.createMimeMessage();
            var helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            helper.setFrom("narindraTsiry18@gmail.com");
            mailSender.send(message);
            log.info("Email envoye a {}", to);
            return true;
        } catch (Exception e) {
            log.error("Erreur envoi email: {}", e.getMessage());
            return false;
        }
    }

    public boolean sendVerificationCode(String to, String code) {
        String subject = "Code de verification - L'Erudition";
        String htmlContent = """
            <!DOCTYPE html>
            <html>
            <head><meta charset="UTF-8"></head>
            <body style="font-family: Arial, sans-serif; background: #f4f4f4; padding: 20px;">
                <div style="max-width: 500px; margin: auto; background: white; border-radius: 15px; box-shadow: 0 4px 10px rgba(0,0,0,0.1); overflow: hidden;">
                    <div style="background: #1e3a8a; color: white; padding: 25px; text-align: center;">
                        <h1 style="margin: 0; font-size: 24px;">L'Erudition</h1>
                        <p style="margin: 5px 0 0; opacity: 0.9;">Verification de votre email</p>
                    </div>
                    <div style="padding: 30px; text-align: center;">
                        <h2 style="color: #1e3a8a; margin: 0;">Votre code de verification</h2>
                        <p style="color: #666; margin: 15px 0;">Utilisez ce code pour valider votre inscription :</p>
                        <div style="background: #f0f4ff; border: 2px dashed #1e3a8a; border-radius: 15px; padding: 20px; margin: 25px 0;">
                            <span style="font-size: 40px; font-weight: 900; color: #1e3a8a; letter-spacing: 12px;">%s</span>
                        </div>
                        <p style="color: #ef4444; font-size: 13px;">Ce code expire dans <strong>10 minutes</strong>.</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(code);
        return sendHtmlEmail(to, subject, htmlContent);
    }

    public boolean sendWelcomeEmail(String to, String prenom) {
        String subject = "Bienvenue sur L'Erudition";
        String htmlContent = """
            <!DOCTYPE html>
            <html>
            <head><meta charset="UTF-8"></head>
            <body style="font-family: Arial, sans-serif; background: #f4f4f4; padding: 20px;">
                <div style="max-width: 500px; margin: auto; background: white; border-radius: 15px; overflow: hidden;">
                    <div style="background: #10b981; color: white; padding: 25px; text-align: center;">
                        <h1 style="margin: 0;">Inscription reussie</h1>
                    </div>
                    <div style="padding: 30px;">
                        <h2>Bonjour %s,</h2>
                        <p>Votre compte sur <strong>L'Erudition</strong> a ete cree avec succes.</p>
                        <div style="text-align: center; margin: 25px 0;">
                            <a href="http://localhost:8080/login" style="background: #1e3a8a; color: white; padding: 12px 30px; text-decoration: none; border-radius: 25px; font-weight: bold;">
                                Se connecter
                            </a>
                        </div>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(prenom);
        return sendHtmlEmail(to, subject, htmlContent);
    }

    public boolean sendPasswordResetEmail(String to, String resetToken) {
        String subject = "Reinitialisation de mot de passe - L'Erudition";
        String resetLink = "http://localhost:8080/reset-password?token=" + resetToken;
        String htmlContent = """
            <!DOCTYPE html>
            <html>
            <head><meta charset="UTF-8"></head>
            <body style="font-family: Arial, sans-serif; background: #f4f4f4; padding: 20px;">
                <div style="max-width: 500px; margin: auto; background: white; border-radius: 15px; overflow: hidden;">
                    <div style="background: #1e3a8a; color: white; padding: 25px; text-align: center;">
                        <h1 style="margin: 0;">L'Erudition</h1>
                    </div>
                    <div style="padding: 30px;">
                        <h2>Reinitialisation de mot de passe</h2>
                        <div style="text-align: center; margin: 25px 0;">
                            <a href="%s" style="background: #1e3a8a; color: white; padding: 12px 30px; text-decoration: none; border-radius: 25px; font-weight: bold;">
                                Reinitialiser
                            </a>
                        </div>
                        <p style="color: red; font-size: 12px;">Expire dans 1 heure.</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(resetLink);
        return sendHtmlEmail(to, subject, htmlContent);
    }

    public boolean sendEmailWithAttachment(String to, String subject, String htmlContent, String attachmentName, byte[] attachmentBytes) {
        try {
            var message = mailSender.createMimeMessage();
            var helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            helper.setFrom("narindraTsiry18@gmail.com");
            
            if (attachmentBytes != null) {
                helper.addAttachment(attachmentName, new org.springframework.core.io.ByteArrayResource(attachmentBytes));
            }
            
            mailSender.send(message);
            log.info("Email avec pièce jointe envoyé à {}", to);
            return true;
        } catch (Exception e) {
            log.error("Erreur envoi email avec pièce jointe: {}", e.getMessage());
            return false;
        }
    }

    public boolean sendQuizCertificateEmail(String to,
                                            String nomEtudiant,
                                            String coursTitre,
                                            String quizTitre,
                                            double score,
                                            String certificatUrl,
                                            byte[] pdfBytes) {
        String subject = "Votre certificat - L'Erudition";
        String htmlContent = """
            <!DOCTYPE html>
            <html>
            <head><meta charset="UTF-8"></head>
            <body style="font-family: Arial, sans-serif; background: #f4f6fb; padding: 20px;">
                <div style="max-width: 620px; margin: auto; background: #ffffff; border-radius: 16px; overflow: hidden; border: 1px solid #e5e7eb;">
                    <div style="background: #1e3a8a; color: #fff; padding: 20px 24px; text-align: center;">
                        <div style="display: inline-flex; align-items: center; gap: 10px; margin-bottom: 8px;">
                            <span style="display:inline-flex; align-items:center; justify-content:center; width:36px; height:36px; border-radius:10px; background:#ffffff; color:#1e3a8a; font-weight:800;">LE</span>
                            <span style="font-size: 22px; font-weight: 800;">L'Erudition</span>
                        </div>
                        <p style="margin: 0; opacity: 0.9;">Validation de quiz reussie</p>
                    </div>
                    <div style="padding: 28px;">
                        <p style="margin: 0 0 12px; color: #111827;">Bonjour <strong>%s</strong>,</p>
                        <p style="margin: 0 0 16px; color: #374151;">
                            Felicitations, vous avez valide le quiz <strong>%s</strong> du cours <strong>%s</strong>
                            avec un score de <strong>%.2f%%</strong>.
                        </p>
                        <p style="margin: 0 0 16px; color: #374151;">
                            Vous trouverez votre <strong>certificat officiel en pièce jointe</strong> de cet email au format PDF.
                        </p>
                        <div style="text-align: center; margin: 28px 0;">
                            <a href="%s" style="display: inline-block; background: #ffffff; color: #1e3a8a; border: 2px solid #1e3a8a; padding: 12px 22px; text-decoration: none; border-radius: 12px; font-weight: 700;">
                                Voir sur mon compte
                            </a>
                        </div>
                        <div style="margin-top: 24px; padding-top: 20px; border-top: 1px solid #e5e7eb;">
                            <p style="margin: 0; color: #6b7280; font-size: 12px;">Signature numerique</p>
                            <p style="margin: 4px 0 0; color: #1f2937; font-weight: 700;">Direction Pedagogique - L'Erudition</p>
                        </div>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(nomEtudiant, quizTitre, coursTitre, score, certificatUrl);
        
        if (pdfBytes != null) {
            return sendEmailWithAttachment(to, subject, htmlContent, "certificat-" + quizTitre.replace(" ", "_") + ".pdf", pdfBytes);
        }
        return sendHtmlEmail(to, subject, htmlContent);
    }
}
