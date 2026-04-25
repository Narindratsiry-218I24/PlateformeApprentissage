package com.plateforme_etudiant.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // Désactiver TOUTE la sécurité Spring
        http
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()  // Toutes les URLs sont accessibles sans authentification
                )
                .csrf(csrf -> csrf.disable())  // Désactiver CSRF
                .headers(headers -> headers.disable())  // Désactiver les en-têtes
                .formLogin(form -> form.disable())  // Désactiver le formulaire par défaut
                .httpBasic(basic -> basic.disable())  // Désactiver l'authentification basique
                .logout(logout -> logout.disable());  // Désactiver la déconnexion par défaut

        return http.build();
    }
}