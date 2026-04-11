package com.cambofreelance.websiteservice.keycloak.client;

import com.cambofreelance.websiteservice.keycloak.KeycloakEndpoints;
import com.cambofreelance.websiteservice.keycloak.KeycloakProperties;
import com.cambofreelance.websiteservice.keycloak.KeycloakRedisCache;
import com.cambofreelance.websiteservice.keycloak.dto.ClientCredentialsToken;
import com.cambofreelance.websiteservice.keycloak.dto.UmaTokenResponse;
import com.cambofreelance.websiteservice.logger.contants.ErrorCode;
import com.cambofreelance.websiteservice.logger.exceptions.AppException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Component
@RequiredArgsConstructor
public class KeycloakUmaClient {

    private final WebClient keycloakWebClient;
    private final KeycloakProperties props;
    private final KeycloakEndpoints endpoints;
    private final KeycloakRedisCache cache;

    private static String strip(String bearer) {
        return bearer.startsWith("Bearer ") ? bearer.substring(7) : bearer;
    }

    public ClientCredentialsToken clientCredentials() {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "client_credentials");
        form.add("client_id", props.getAudienceClientId());
        form.add("client_secret", props.getAudienceClientSecret());

        ClientCredentialsToken token = keycloakWebClient.post()
            .uri(endpoints.tokenPath())
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .bodyValue(form)
            .retrieve()
            .bodyToMono(ClientCredentialsToken.class)
            .block();

        if (token == null || token.getAccessToken() == null) {
            throw new AppException(ErrorCode.ACCESS_DENIED);
        }
        return token;
    }

    public UmaTokenResponse exchangeEntitlements(String userBearer) {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "urn:ietf:params:oauth:grant-type:uma-ticket");
        form.add("audience", props.getAudienceClientId());
        form.add("response_include_resource_name",
            Boolean.toString(props.isIncludeResourceName()));

        return keycloakWebClient.post()
            .uri(endpoints.tokenPath())
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .headers(h -> h.setBearerAuth(strip(userBearer)))
            .bodyValue(form)
            .retrieve()
            .bodyToMono(UmaTokenResponse.class)
            .block();
    }

    public UmaTokenResponse exchangeSpecificPermission(String userBearer, String permission) {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "urn:ietf:params:oauth:grant-type:uma-ticket");
        form.add("audience", props.getAudienceClientId());
        form.add("permission", permission);
        form.add("response_include_resource_name",
            Boolean.toString(props.isIncludeResourceName()));
        if ("uri".equalsIgnoreCase(props.getPermissionResourceFormat())) {
            form.add("permission_resource_format", "uri");
        }

        return keycloakWebClient.post()
            .uri(endpoints.tokenPath())
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .headers(h -> h.setBearerAuth(strip(userBearer)))
            .bodyValue(form)
            .retrieve()
            .bodyToMono(UmaTokenResponse.class)
            .block();
    }
}
