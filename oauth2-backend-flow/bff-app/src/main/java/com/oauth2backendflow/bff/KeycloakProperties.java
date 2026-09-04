package com.oauth2backendflow.bff;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "keycloak")
public record KeycloakProperties(
        String baseUrl,
        String realm,
        String clientId,
        String clientSecret,
        String redirectUri,
        String postLogoutRedirectUri
) {
    public String issuerUri() {
        return baseUrl + "/realms/" + realm;
    }

    public String authorizationEndpoint() {
        return issuerUri() + "/protocol/openid-connect/auth";
    }

    public String tokenEndpoint() {
        return issuerUri() + "/protocol/openid-connect/token";
    }

    public String endSessionEndpoint() {
        return issuerUri() + "/protocol/openid-connect/logout";
    }
}
