package com.cambofreelance.websiteservice.keycloak.service.impl;

import com.cambofreelance.websiteservice.keycloak.KeycloakProperties;
import com.cambofreelance.websiteservice.keycloak.KeycloakRedisCache;
import com.cambofreelance.websiteservice.keycloak.client.KeycloakResourceClient;
import com.cambofreelance.websiteservice.keycloak.client.KeycloakUmaClient;
import com.cambofreelance.websiteservice.keycloak.dto.AuthorizationDecision;
import com.cambofreelance.websiteservice.keycloak.dto.UmaTokenResponse;
import com.cambofreelance.websiteservice.keycloak.service.UmaAuthorizationService;
import com.cambofreelance.websiteservice.keycloak.support.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
@Slf4j
public class UmaAuthorizationServiceImpl implements UmaAuthorizationService {

    private static final String ID_PREFIX = "id:";
    private static final String URI_PREFIX = "uri:";

    private final KeycloakProperties props;
    private final KeycloakUmaClient uma;
    private final KeycloakResourceClient res;
    private final KeycloakRedisCache cache;

    @Override
    public AuthorizationDecision ensureAuthorizedByName(String userBearer, String resourceName, String scope) {
        if (!StringUtils.hasText(resourceName) || !StringUtils.hasText(scope)) {
            log.warn("Missing resourceName or scope for authorization check");
            return new AuthorizationDecision(false, null, "invalid_request", "Missing resourceName/scope");
        }
        if (!StringUtils.hasText(userBearer)) {
            log.warn("Missing bearer token for authorization check");
            return new AuthorizationDecision(false, null, "invalid_request", "Missing token");
        }

        String resourceId = res.resourceIdByName(resourceName);
        if (resourceId == null) {
            log.warn("Resource not found: {}", resourceName);
            return new AuthorizationDecision(false, null, "invalid_resource", "Resource not found");
        }
        return ensureAuthorizedById(userBearer, resourceId, scope);
    }

    @Override
    public AuthorizationDecision ensureAuthorizedById(String userBearer, String resourceId, String scope) {
        return ensureAuthorized(userBearer, ID_PREFIX + resourceId + "#" + scope,
                () -> props.isUseEntitlements()
                        ? uma.exchangeEntitlements(userBearer)
                        : uma.exchangeSpecificPermission(userBearer, resourceId + "#" + scope));
    }

    @Override
    public AuthorizationDecision ensureAuthorizedByUri(String userBearer, String resourceUri, String scope) {
        return ensureAuthorized(userBearer, URI_PREFIX + resourceUri + "#" + scope,
                () -> uma.exchangeSpecificPermission(userBearer, resourceUri + "#" + scope));
    }

    private AuthorizationDecision ensureAuthorized(String userBearer, String permKey, Supplier<UmaTokenResponse> exchangeFunc) {
        String sub = JwtUtils.subject(userBearer);
        if (!StringUtils.hasText(sub)) {
            log.warn("Invalid JWT token, cannot extract subject");
            return new AuthorizationDecision(false, null, "invalid_token", "Cannot extract subject from token");
        }

        String rpt = cache.getRpt(sub, props.getAudienceClientId(), permKey);
        if (rpt != null) {
            log.debug("Authorization cache hit for sub={} permKey={}", sub, permKey);
            return new AuthorizationDecision(true, rpt, "authorized", "cached");
        }

        UmaTokenResponse resp;
        try {
            resp = exchangeFunc.get();
        } catch (Exception e) {
            log.error("Error calling UMA client for sub={} permKey={}", sub, permKey, e);
            return new AuthorizationDecision(false, null, "client_error", e.getMessage());
        }

        return setAuthorizationDecisionFromUmaTokenResponse(resp, sub, permKey);
    }

    private AuthorizationDecision setAuthorizationDecisionFromUmaTokenResponse(UmaTokenResponse resp, String sub, String permKey) {
        if (resp != null && StringUtils.hasText(resp.getAccessToken())) {
            cache.cacheRpt(sub, props.getAudienceClientId(), permKey, resp.getAccessToken(), resp.getExpiresIn());
            log.debug("Authorization successful for sub={} permKey={}", sub, permKey);
            return new AuthorizationDecision(true, resp.getAccessToken(), "authorized", "fresh");
        }

        String reason = resp == null ? "authorization_failed" : resp.getError();
        String msg = resp == null ? "no response" : resp.getErrorDescription();
        log.warn("Authorization failed for sub={} permKey={}, reason={}, msg={}", sub, permKey, reason, msg);
        return new AuthorizationDecision(false, null, reason, msg);
    }
}
