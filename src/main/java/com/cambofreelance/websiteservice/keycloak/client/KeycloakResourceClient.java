package com.cambofreelance.websiteservice.keycloak.client;

import com.cambofreelance.websiteservice.keycloak.KeycloakEndpoints;
import com.cambofreelance.websiteservice.keycloak.KeycloakProperties;
import com.cambofreelance.websiteservice.keycloak.KeycloakRedisCache;
import com.cambofreelance.websiteservice.keycloak.dto.ClientCredentialsToken;
import com.cambofreelance.websiteservice.keycloak.dto.UserResponse;
import com.cambofreelance.websiteservice.logger.contants.ErrorCode;
import com.cambofreelance.websiteservice.logger.exceptions.AppException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class KeycloakResourceClient {

    private final WebClient keycloakWebClient;
    private final KeycloakProperties props;
    private final KeycloakEndpoints endpoints;
    private final KeycloakRedisCache cache;
    private final KeycloakUmaClient umaClient;

    public String resourceIdByName(String resourceName) {
        try {
            ClientCredentialsToken cc = umaClient.clientCredentials();
            List<String> ids = keycloakWebClient.get()
                .uri(uriBuilder -> uriBuilder
                    .path(endpoints.resourceSearchPath())
                    .queryParam("name", resourceName)
                    .queryParam("exactName", "true")
                    .build())
                .accept(MediaType.APPLICATION_JSON)
                .headers(h -> h.setBearerAuth(cc.getAccessToken()))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<String>>() {
                })
                .block();
            if (ids == null || ids.isEmpty()) {
                return null;
            }
            return ids.getFirst();
        } catch (Exception e) {
            throw new AppException(ErrorCode.ACCESS_DENIED);
        }
    }

    public UserResponse introspectToken(String token) {
        return keycloakWebClient.post()
            .uri(endpoints.introspectPath())
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
            .body(BodyInserters
                .fromFormData("client_id", props.getAudienceClientId())
                .with("client_secret", props.getAudienceClientSecret())
                .with("token", token))
            .retrieve()
            .bodyToMono(new ParameterizedTypeReference<UserResponse>() {
            })
            .block();
    }

}
