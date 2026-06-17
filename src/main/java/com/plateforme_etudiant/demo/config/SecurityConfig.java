package com.plateforme_etudiant.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/",
                                "/login",
                                "/logout",
                                "/inscription/**",
                                "/forgot-password",
                                "/reset-password",
                                "/api/check-email",
                                "/css/**",
                                "/js/**",
                                "/images/**",
                                "/uploads/**",
                                "/webjars/**",
                                "/error",
                                "/ws/**",
                                "/ws"
                        ).permitAll()
                        .requestMatchers("/messages/**", "/messages").hasAnyRole("PROFESSEUR", "APPRENANT", "ADMINISTRATEUR")
                        .requestMatchers("/professeur/**").hasRole("PROFESSEUR")
                        .requestMatchers("/etudiant/**").hasRole("APPRENANT")
                        .requestMatchers("/admin/**").hasRole("ADMINISTRATEUR")
                        .anyRequest().authenticated()
                )
                .csrf(csrf -> csrf.disable())
                .headers(headers -> headers.disable())
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/login"))
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout=true")
                        .permitAll()
                );

        return http.build();
    }
}