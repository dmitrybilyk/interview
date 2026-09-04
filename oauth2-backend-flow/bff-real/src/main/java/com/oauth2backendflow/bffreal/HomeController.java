package com.oauth2backendflow.bffreal;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.client.RestClient;

@Controller
public class HomeController {

    @Value("${spring.security.oauth2.client.provider.keycloak.issuer-uri}")
    private String issuerUri;

    @Value("${spring.security.oauth2.client.registration.keycloak.client-id}")
    private String clientId;

    @Value("${app.post-logout-redirect-uri}")
    private String postLogoutRedirectUri;

    @Value("${resource-server.base-url}")
    private String resourceServerBaseUrl;

    private final RestClient restClient = RestClient.create();
    private final ObjectMapper json = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .enable(SerializationFeature.INDENT_OUTPUT);

    @GetMapping("/")
    public String index(Authentication auth, Model model) {
        model.addAttribute("loggedIn", auth != null && auth.isAuthenticated());
        return "index";
    }

    @GetMapping("/secured")
    public String secured(
            @RegisteredOAuth2AuthorizedClient("keycloak") OAuth2AuthorizedClient client,
            @AuthenticationPrincipal OidcUser user,
            Model model) throws Exception {
        model.addAttribute("name", user.getFullName() != null ? user.getFullName() : user.getName());
        model.addAttribute("email", user.getEmail());
        model.addAttribute("claims", json.writeValueAsString(user.getClaims()));
        model.addAttribute("accessTokenMasked", mask(client.getAccessToken().getTokenValue()));
        model.addAttribute("accessTokenExpiry", client.getAccessToken().getExpiresAt());
        return "secured";
    }

    @PostMapping("/call-api")
    public String callApi(
            @RegisteredOAuth2AuthorizedClient("keycloak") OAuth2AuthorizedClient client,
            @AuthenticationPrincipal OidcUser user,
            Model model) throws Exception {
        String token = client.getAccessToken().getTokenValue();
        String uri   = resourceServerBaseUrl + "/api/protected-data";

        String raw = restClient.get()
                .uri(uri)
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .body(String.class);

        model.addAttribute("name", user.getFullName() != null ? user.getFullName() : user.getName());
        model.addAttribute("sentTo", uri);
        model.addAttribute("bearerMasked", mask(token));
        model.addAttribute("response", json.writeValueAsString(json.readTree(raw)));
        return "api-result";
    }

    @GetMapping("/keycloak-logout")
    public String keycloakLogout(HttpSession session) {
        session.invalidate();
        return "redirect:" + issuerUri + "/protocol/openid-connect/logout"
                + "?client_id=" + clientId
                + "&post_logout_redirect_uri=" + postLogoutRedirectUri;
    }

    private static String mask(String token) {
        if (token == null || token.length() < 20) return "***";
        return token.substring(0, 12) + "...(masked)..." + token.substring(token.length() - 6);
    }
}
