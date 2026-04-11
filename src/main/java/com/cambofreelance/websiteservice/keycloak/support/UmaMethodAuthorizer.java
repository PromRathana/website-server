package com.cambofreelance.websiteservice.keycloak.support;
import com.cambofreelance.websiteservice.keycloak.dto.AuthorizationDecision;
import com.cambofreelance.websiteservice.keycloak.service.UmaAuthorizationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Slf4j
@Component("uma")
@RequiredArgsConstructor
public class UmaMethodAuthorizer {

    private final UmaAuthorizationService uma;

    public boolean has(Authentication authentication, String resource, String scope) {
        if (!(authentication instanceof JwtAuthenticationToken jwt)) return false;
        String bearer = "Bearer " + jwt.getToken().getTokenValue();

        try {
            AuthorizationDecision d = uma.ensureAuthorizedByName(bearer, resource, scope);
            return d.isAuthorized();
        } catch (WebClientResponseException.Forbidden e) {
            return false;
        }
    }

    public boolean uri(Authentication authentication, String resourceUri, String scope) {
        if (!(authentication instanceof JwtAuthenticationToken jwt)) return false;
        String bearer = "Bearer " + jwt.getToken().getTokenValue();

        try {
            AuthorizationDecision d = uma.ensureAuthorizedByUri(bearer, resourceUri, scope);
            return d.isAuthorized();
        } catch (WebClientResponseException.Forbidden e) {
            return false;
        }
    }
}
