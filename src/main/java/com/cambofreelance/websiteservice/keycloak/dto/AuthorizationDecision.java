package com.cambofreelance.websiteservice.keycloak.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor
public class AuthorizationDecision {
    private boolean authorized;
    private String rpt;
    private String reason;
    private String message;
}
