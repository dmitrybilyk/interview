package com.aouth2.authclient;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ThymLeafController {

    @GetMapping("/")
    public String index(Model model, @AuthenticationPrincipal OidcUser user) {
        if (user != null) {
            // Передаємо ім'я користувача з Keycloak у шаблон Thymeleaf
            model.addAttribute("username", user.getFullName());
            model.addAttribute("email", user.getEmail());
        }
        return "index"; // Шукає src/main/resources/templates/index.html
    }
}
