package com.cambofreelance.websiteservice.keycloak.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class UmaTokenResponse {
    @JsonProperty("access_token") private String accessToken; // RPT
    @JsonProperty("expires_in") private int expiresIn;
    @JsonProperty("token_type") private String tokenType;
    @JsonProperty("error") private String error;
    @JsonProperty("error_description") private String errorDescription;
}
