package com.cambofreelance.websiteservice.keycloak;

import com.cambofreelance.websiteservice.keycloak.dto.ClientCredentialsToken;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

@Repository
@Slf4j
@RequiredArgsConstructor
public class KeycloakRedisCache {

    private final RedisTemplate<String, String> redis;
    private final ObjectMapper om;

    private static final String PREFIX = "KC";

    private static final String CC_TOKEN = PREFIX + ":CC:TOKEN";
    private static final String RES_ID_FMT = PREFIX + ":RESID:%s";
    private static final String RPT_FMT = PREFIX + ":RPT:%s:%s:%s";
    // sub:audience:permission

    /**
     * Cache client credentials token
     */
    public void cacheClientCredentials(ClientCredentialsToken token) {

        int ttl = safeTtl(token.getExpiresIn());

        setJson(CC_TOKEN, token, ttl);
    }

    public ClientCredentialsToken getClientCredentials() {
        return getJson(CC_TOKEN, ClientCredentialsToken.class);
    }

    /**
     * Cache resource id
     */
    public void cacheResourceId(String resourceName, String resourceId) {

        String key = RES_ID_FMT.formatted(resourceName);

        redis.opsForValue()
            .set(key, resourceId, Duration.ofHours(1));
    }

    public String getResourceId(String resourceName) {

        return redis.opsForValue()
            .get(RES_ID_FMT.formatted(resourceName));
    }

    /**
     * Cache RPT token
     */
    public void cacheRpt(String sub, String audience, String permission, String rpt, int expiresIn) {

        String key = RPT_FMT.formatted(sub, audience, permission);

        int ttl = safeTtl(expiresIn);

        redis.opsForValue()
            .set(key, rpt, Duration.ofSeconds(ttl));
    }

    public String getRpt(String sub, String audience, String permission) {

        String key = RPT_FMT.formatted(sub, audience, permission);

        return redis.opsForValue().get(key);
    }

    /**
     * Store JSON object
     */
    private <T> void setJson(String key, T value, int ttlSec) {

        try {

            String json = om.writeValueAsString(value);

            redis.opsForValue()
                .set(key, json, Duration.ofSeconds(ttlSec));

        } catch (Exception ex) {

            log.warn("Redis setJson failed key={} error={}", key, ex.getMessage());

        }
    }

    /**
     * Read JSON object
     */
    private <T> T getJson(String key, Class<T> type) {

        try {

            String json = redis.opsForValue().get(key);

            if (json == null) {
                return null;
            }

            return om.readValue(json, type);

        } catch (Exception ex) {

            log.warn("Redis getJson failed key={} error={}", key, ex.getMessage());

            return null;
        }
    }

    /**
     * Safe TTL calculation
     */
    private int safeTtl(int expiresIn) {

        if (expiresIn <= 60) {
            return 60;
        }

        return expiresIn - 60;
    }

}