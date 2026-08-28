package com.oauth2backendflow.bff;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;

/**
 * Manual, step-by-step Authorization Code flow (BFF pattern) — see oauth2/puml/BFF(client).puml.
 * Deliberately does NOT use spring-boot-starter-oauth2-client's oauth2Login(): every HTTP hop is
 * built and executed by hand here so it can be shown to the user instead of hidden behind a filter.
 */
@Controller
public class FlowController {

    private static final String SESSION_TRACE = "trace";
    private static final String SESSION_STATE = "oauth_state";
    private static final String SESSION_TOKENS = "tokens";

    private final KeycloakProperties keycloak;
    private final String resourceServerBaseUrl;
    private final RestClient restClient = RestClient.create();
    private final ObjectMapper objectMapper = new ObjectMapper().enable(com.fasterxml.jackson.databind.SerializationFeature.INDENT_OUTPUT);

    public FlowController(KeycloakProperties keycloak,
                           @org.springframework.beans.factory.annotation.Value("${resource-server.base-url}") String resourceServerBaseUrl) {
        this.keycloak = keycloak;
        this.resourceServerBaseUrl = resourceServerBaseUrl;
    }

    @GetMapping("/")
    public String index(HttpSession session, Model model) {
        Map<String, Object> tokens = tokens(session);
        model.addAttribute("loggedIn", tokens != null);
        model.addAttribute("tokens", tokens);
        model.addAttribute("trace", trace(session));
        return "index";
    }

    @GetMapping("/login")
    public String login(HttpSession session, Model model) {
        String state = randomToken();
        session.setAttribute(SESSION_STATE, state);

        String authorizeUrl = keycloak.authorizationEndpoint()
                + "?client_id=" + enc(keycloak.clientId())
                + "&redirect_uri=" + enc(keycloak.redirectUri())
                + "&response_type=code"
                + "&scope=" + enc("openid profile email")
                + "&state=" + enc(state);

        addTrace(session, "1. Backend будує Authorization Request",
                "(нічого ще не відправлено — це URL, куди піде браузер)",
                "GET " + authorizeUrl);

        model.addAttribute("authorizeUrl", authorizeUrl);
        model.addAttribute("trace", trace(session));
        return "login";
    }

    @GetMapping("/callback")
    public String callback(@RequestParam("code") String code,
                            @RequestParam(value = "state", required = false) String state,
                            @RequestParam Map<String, String> allParams,
                            HttpSession session,
                            Model model) {
        String expectedState = (String) session.getAttribute(SESSION_STATE);
        boolean stateOk = expectedState != null && expectedState.equals(state);

        addTrace(session, "2. Keycloak редіректнув браузер назад із code",
                "браузер сам прийшов на GET /callback з query-параметрами нижче",
                "code=" + code + "\nstate=" + state + (stateOk ? "  (співпав з тим, що ми зберігали)" : "  (!!! НЕ співпадає)")
                        + "\n" + allParams);

        model.addAttribute("code", code);
        model.addAttribute("stateOk", stateOk);
        model.addAttribute("trace", trace(session));
        return "callback";
    }

    @PostMapping("/exchange")
    public String exchange(@RequestParam("code") String code, HttpSession session, Model model) {
        String body = "grant_type=authorization_code"
                + "&client_id=" + enc(keycloak.clientId())
                + "&client_secret=" + enc(keycloak.clientSecret())
                + "&code=" + enc(code)
                + "&redirect_uri=" + enc(keycloak.redirectUri());

        String maskedBody = body.replace(enc(keycloak.clientSecret()), "***MASKED***");

        String rawResponse = restClient.post()
                .uri(keycloak.tokenEndpoint())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(body)
                .retrieve()
                .body(String.class);

        Map<String, Object> tokens = readJson(rawResponse);
        session.setAttribute(SESSION_TOKENS, tokens);

        addTrace(session, "3. Backend обміняв code на токени (back-channel, з client_secret)",
                "POST " + keycloak.tokenEndpoint() + "\n" + maskedBody,
                prettyJson(rawResponse));

        model.addAttribute("rawResponse", prettyJson(rawResponse));
        model.addAttribute("trace", trace(session));
        return "exchange-result";
    }

    @GetMapping("/api/data")
    public String apiData(HttpServletRequest request, HttpSession session, Model model) {
        Map<String, Object> tokens = tokens(session);
        if (tokens == null) {
            String cookieHeader = request.getHeader("Cookie");
            addTrace(session, "0. FE спробував GET /api/data → Backend повернув 401",
                    "GET /api/data\nCookie: " + (cookieHeader != null ? cookieHeader : "(відсутній — нова сесія)"),
                    "HTTP 401 Unauthorized\n(Сесія не містить токенів. Потрібна автентифікація.)");
            model.addAttribute("trace", trace(session));
            return "unauthorized";
        }

        String idToken = (String) tokens.get("id_token");
        String claims = idToken != null ? prettyJson(decodeJwtPayload(idToken)) : "(немає id_token)";

        addTrace(session, "4. 'Frontend' викликав Backend GET /api/data лише з session cookie",
                "GET /api/data\nCookie: JSESSIONID=" + session.getId() + "\n(жодного токена в запиті браузера немає!)",
                "Backend сам дістав id_token із СВОЄЇ сесії і повернув дані:\n" + claims);

        model.addAttribute("claims", claims);
        model.addAttribute("trace", trace(session));
        return "api-data";
    }

    @PostMapping("/call-downstream")
    public String callDownstream(HttpSession session, Model model) {
        Map<String, Object> tokens = tokens(session);
        if (tokens == null) {
            return "redirect:/";
        }
        String accessToken = (String) tokens.get("access_token");

        String uri = resourceServerBaseUrl + "/api/protected-data";
        String rawResponse = restClient.get()
                .uri(URI.create(uri))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .retrieve()
                .body(String.class);

        addTrace(session, "5. Backend (як Client!) викликав окремий Resource Server",
                "GET " + uri + "\nAuthorization: Bearer " + mask(accessToken),
                prettyJson(rawResponse));

        model.addAttribute("rawResponse", prettyJson(rawResponse));
        model.addAttribute("trace", trace(session));
        return "downstream-result";
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    @GetMapping("/keycloak-logout")
    public String keycloakLogoutPage(HttpSession session, Model model) {
        String url = keycloak.endSessionEndpoint()
                + "?client_id=" + enc(keycloak.clientId())
                + "&post_logout_redirect_uri=" + enc(keycloak.postLogoutRedirectUri());
        model.addAttribute("logoutUrl", url);
        model.addAttribute("trace", trace(session));
        return "keycloak-logout";
    }

    // --- helpers -------------------------------------------------------

    @SuppressWarnings("unchecked")
    private Map<String, Object> tokens(HttpSession session) {
        return (Map<String, Object>) session.getAttribute(SESSION_TOKENS);
    }

    @SuppressWarnings("unchecked")
    private List<TraceEntry> trace(HttpSession session) {
        List<TraceEntry> list = (List<TraceEntry>) session.getAttribute(SESSION_TRACE);
        return list != null ? list : List.of();
    }

    @SuppressWarnings("unchecked")
    private void addTrace(HttpSession session, String title, String request, String response) {
        List<TraceEntry> list = (List<TraceEntry>) session.getAttribute(SESSION_TRACE);
        if (list == null) {
            list = new ArrayList<>();
        }
        list.add(TraceEntry.of(title, request, response));
        session.setAttribute(SESSION_TRACE, list);
    }

    private static String enc(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    private static String randomToken() {
        byte[] bytes = new byte[16];
        new SecureRandom().nextBytes(bytes);
        return HexFormat.of().formatHex(bytes);
    }

    private static String mask(String token) {
        if (token == null || token.length() < 16) {
            return "***";
        }
        return token.substring(0, 12) + "...(masked)...";
    }

    private Map<String, Object> readJson(String json) {
        try {
            return objectMapper.readValue(json, Map.class);
        } catch (Exception e) {
            throw new IllegalStateException("Не вдалось розпарсити відповідь Keycloak: " + json, e);
        }
    }

    private String prettyJson(String json) {
        try {
            Object tree = objectMapper.readTree(json);
            return objectMapper.writeValueAsString(tree);
        } catch (Exception e) {
            return json;
        }
    }

    /** Decodes (WITHOUT verifying) the payload segment of a JWT — fine here, the id_token came
     *  straight from Keycloak over the back-channel, the backend already trusts the source. */
    private String decodeJwtPayload(String jwt) {
        String[] parts = jwt.split("\\.");
        if (parts.length < 2) {
            return "{}";
        }
        byte[] decoded = Base64.getUrlDecoder().decode(parts[1]);
        return new String(decoded, StandardCharsets.UTF_8);
    }
}
