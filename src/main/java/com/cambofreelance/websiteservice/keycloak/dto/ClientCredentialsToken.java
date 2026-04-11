package com.cambofreelance.websiteservice.keycloak.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ClientCredentialsToken {
    @JsonProperty("access_token") private String accessToken;
    @JsonProperty("expires_in") private int expiresIn;
    @JsonProperty("refresh_expires_in") private int refreshExpiresIn;
    @JsonProperty("token_type") private String tokenType;
    @JsonProperty("scope") private String scope;
    @JsonProperty("error") private String error;
    @JsonProperty("error_description") private String errorDescription;
}
