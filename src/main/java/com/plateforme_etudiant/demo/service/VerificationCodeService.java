package com.plateforme_etudiant.demo.service;

import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class VerificationCodeService {

    private final SecureRandom random = new SecureRandom();
    private final Map<String, CodeInfo> codeStore = new ConcurrentHashMap<>();

    /**
     * Génère un code à 6 chiffres
     */
    public String generateCode() {
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }

    /**
     * Stocke le code pour un email (valide 10 minutes)
     */
    public void storeCode(String email, String code) {
        CodeInfo codeInfo = new CodeInfo(
                code,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(10),
                3 // 3 tentatives max
        );
        codeStore.put(email, codeInfo);
    }

    /**
     * Vérifie le code pour un email
     * @return true si le code est valide
     */
    public boolean verifyCode(String email, String code) {
        CodeInfo codeInfo = codeStore.get(email);

        if (codeInfo == null) {
            return false; // Pas de code pour cet email
        }

        if (LocalDateTime.now().isAfter(codeInfo.expirationTime)) {
            codeStore.remove(email);
            return false; // Code expiré
        }

        if (codeInfo.attempts <= 0) {
            codeStore.remove(email);
            return false; // Trop de tentatives
        }

        codeInfo.attempts--;

        if (!codeInfo.code.equals(code)) {
            return false; // Code incorrect
        }

        // Code valide, on le supprime
        codeStore.remove(email);
        return true;
    }

    /**
     * Supprime un code
     */
    public void removeCode(String email) {
        codeStore.remove(email);
    }

    private static class CodeInfo {
        private final String code;
        private final LocalDateTime creationTime;
        private final LocalDateTime expirationTime;
        private int attempts;

        public CodeInfo(String code, LocalDateTime creationTime, LocalDateTime expirationTime, int attempts) {
            this.code = code;
            this.creationTime = creationTime;
            this.expirationTime = expirationTime;
            this.attempts = attempts;
        }
    }
}