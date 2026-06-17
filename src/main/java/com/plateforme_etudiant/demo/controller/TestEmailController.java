package com.plateforme_etudiant.demo.controller;

import com.plateforme_etudiant.demo.service.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestEmailController {

    @Autowired
    private MailService mailService;

    @GetMapping("/test-email")
    public String testEmail() {
        boolean sent = mailService.sendHtmlEmail(
                "narindraTsiry18@gmail.com",
                "✅ Test Email - L'Érudition",
                """
                <div style="font-family: Arial; padding: 20px;">
                    <h1 style="color: #1e3a8a;">🎉 Félicitations Narindra !</h1>
                    <p>Votre configuration email fonctionne parfaitement !</p>
                    <p>✅ Spring Boot + Gmail = OK</p>
                    <hr/>
                    <p style="color: #666;">© 2024 L'Érudition</p>
                </div>
                """
        );

        if (sent) {
            return "✅ Email envoyé ! Vérifiez narindraTsiry18@gmail.com";
        } else {
            return "❌ Erreur - Vérifiez la console pour plus de détails";
        }
    }

    @GetMapping("/test-reset-password")
    public String testResetPassword() {
        boolean sent = mailService.sendPasswordResetEmail(
                "narindraTsiry18@gmail.com",
                "mon-token-de-test-123"
        );

        if (sent) {
            return "✅ Email de réinitialisation envoyé ! Vérifiez narindraTsiry18@gmail.com";
        } else {
            return "❌ Erreur - Vérifiez la console";
        }
    }
}