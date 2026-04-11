package com.cambofreelance.websiteservice.keycloak.service;


import com.cambofreelance.websiteservice.keycloak.dto.AuthorizationDecision;

public interface UmaAuthorizationService {
    AuthorizationDecision ensureAuthorizedByName(String userBearer, String resourceName, String scope);
    AuthorizationDecision ensureAuthorizedById(String userBearer, String resourceId, String scope);
    AuthorizationDecision ensureAuthorizedByUri(String userBearer, String resourceUri, String scope);
}
