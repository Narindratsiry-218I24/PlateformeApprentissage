package com.plateforme_etudiant.demo.service;

import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TokenService {

    private final SecureRandom secureRandom = new SecureRandom();
    private final Map<String, TokenInfo> tokenStore = new ConcurrentHashMap<>();

    /**
     * Génère un token sécurisé
     */
    public String generateToken() {
        byte[] tokenBytes = new byte[32];
        secureRandom.nextBytes(tokenBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);
    }

    /**
     * Stocke un token avec sa durée de validité
     */
    public void storeToken(String token, String email, long validityHours) {
        TokenInfo tokenInfo = new TokenInfo(
                email,
                LocalDateTime.now(),
                LocalDateTime.now().plusHours(validityHours)
        );
        tokenStore.put(token, tokenInfo);
    }

    /**
     * Valide un token et retourne l'email associé
     */
    public String validateToken(String token) {
        TokenInfo tokenInfo = tokenStore.get(token);

        if (tokenInfo == null) {
            return null;
        }

        if (LocalDateTime.now().isAfter(tokenInfo.expirationTime)) {
            tokenStore.remove(token);
            return null;
        }

        return tokenInfo.email;
    }

    /**
     * Supprime un token après utilisation
     */
    public void removeToken(String token) {
        tokenStore.remove(token);
    }

    private static class TokenInfo {
        private final String email;
        private final LocalDateTime creationTime;
        private final LocalDateTime expirationTime;

        public TokenInfo(String email, LocalDateTime creationTime, LocalDateTime expirationTime) {
            this.email = email;
            this.creationTime = creationTime;
            this.expirationTime = expirationTime;
        }
    }
}