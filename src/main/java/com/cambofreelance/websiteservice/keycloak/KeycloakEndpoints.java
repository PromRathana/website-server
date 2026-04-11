package com.cambofreelance.websiteservice.keycloak;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KeycloakEndpoints {
    private final KeycloakProperties p;

    public String tokenPath() {
        return "/realms/" + p.getRealm() + "/protocol/openid-connect/token";
    }

    public String resourceSearchPath() {
        // Protection API resource_set
        return "/realms/" + p.getRealm() + "/authz/protection/resource_set";
    }

    public String logoutPath() {
        return "/realms/" + p.getRealm() + "/protocol/openid-connect/logout";
    }

    public String adminGetAllUserPath() {
        return "/admin/realms/" + p.getRealm() + "/users";
    }

    public String adminGetAllRolesPath() {
        return "/admin/realms/" + p.getRealm() + "/roles";
    }

    public String adminAssignAllRolesPath(String roleId) {
        return "/admin/realms/" + p.getRealm() + "/users/" + roleId + "/role-mappings/realm";
    }
    public String adminResetPassword(String userId) {
        return "/admin/realms/" + p.getRealm() + "/users/" + userId + "/reset-password";
    }

    public String introspectPath() {
        return "/realms/" + p.getRealm() + "/protocol/openid-connect/token/introspect";
    }

    public String adminChangePasswordPath() {
        return "/admin/realms/" + p.getRealm() + "/users/";
    }
}
