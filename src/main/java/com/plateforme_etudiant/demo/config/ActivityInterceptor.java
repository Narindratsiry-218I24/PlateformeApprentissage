package com.plateforme_etudiant.demo.config;

import com.plateforme_etudiant.demo.service.UtilisateurService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class ActivityInterceptor implements HandlerInterceptor {

    @Autowired
    private UtilisateurService utilisateurService;

    // Matches /etudiant/cours/123/...
    private static final Pattern COURS_PATTERN = Pattern.compile("^/etudiant/cours/(\\d+)");

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("userId") != null) {
            String role = (String) session.getAttribute("userRole");
            if ("APPRENANT".equals(role)) {
                Long userId = (Long) session.getAttribute("userId");
                String uri = request.getRequestURI();
                
                Long coursId = null;
                Matcher matcher = COURS_PATTERN.matcher(uri);
                if (matcher.find()) {
                    coursId = Long.parseLong(matcher.group(1));
                }

                utilisateurService.mettreAJourActivite(userId, coursId);
            }
        }
        return true;
    }
}
