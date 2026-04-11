package com.cambofreelance.websiteservice.keycloak;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "keycloak")
public class KeycloakProperties {
    @NotBlank private String url;
    @NotBlank private String realm;

    @NotBlank private String audienceClientId;
    @NotBlank private String audienceClientSecret;

    private boolean useEntitlements = false;          // entitlements vs specific permission
    private String permissionResourceFormat = "id";  // id | uri
    private boolean includeResourceName = true;

    private Http http = new Http();
    private Retry retry = new Retry();

    @Data public static class Http {
        private int connectTimeoutMs = 3000;
        private int responseTimeoutMs = 8000;
    }
    @Data public static class Retry {
        private int maxRetries = 3;
        private int delayMs = 500;
    }
}
