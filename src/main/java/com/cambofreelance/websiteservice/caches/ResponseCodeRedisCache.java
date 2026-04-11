package com.cambofreelance.websiteservice.caches;

import com.cambofreelance.websiteservice.logger.dto.ResponseCodeDto;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;

@Slf4j
@Repository
public class ResponseCodeRedisCache {

    @Value("${storage.redis.key-response-code}")
    private String keyValue;

    private String key;

    private HashOperations<String, String, ResponseCodeDto> hashOperations;

    @Resource
    private RedisTemplate<String, ResponseCodeDto> responseCodeRedisTemplate;

    @PostConstruct
    private void init() {
        this.key = keyValue;
        this.hashOperations = responseCodeRedisTemplate.opsForHash();
        log.info("Initialized ResponseCodeRedisCache with type={}", key);
    }

    public ResponseCodeDto getRespCode(final String code) {
        try {
            return hashOperations.get(key, code);
        } catch (DataAccessException ex) {
            log.error("Failed to fetch response code {} from Redis", code, ex);
            return null;
        }
    }

    public void initRespCodeCache(List<ResponseCodeDto> respCodes) {
        if (Objects.isNull(respCodes) || respCodes.isEmpty()) {
            log.warn("initRespCodeCache called with empty or null list");
            return;
        }
        try {
            responseCodeRedisTemplate.delete(key);
            for (ResponseCodeDto responseCode : respCodes) {
                hashOperations.put(key, responseCode.getCode(), responseCode);
            }
            log.info("Initialized Redis cache with {} response codes", respCodes.size());
        } catch (DataAccessException ex) {
            log.error("Failed to initialize response codes in Redis", ex);
        }
    }

    public void addRespCodeCache(final ResponseCodeDto respCode) {
        if (respCode == null || respCode.getCode() == null) {
            log.warn("addRespCodeCache called with null responseCode or code");
            return;
        }
        try {
            hashOperations.put(key, respCode.getCode(), respCode);
            log.debug("Added response code {} to cache", respCode.getCode());
        } catch (DataAccessException ex) {
            log.error("Failed to add response code {} to Redis", respCode.getCode(), ex);
        }
    }

    public void reloadRespCode(final ResponseCodeDto respCode) {
        if (respCode == null || respCode.getCode() == null) {
            log.warn("reloadRespCode called with null responseCode or code");
            return;
        }
        try {
            hashOperations.put(key, respCode.getCode(), respCode);
            log.debug("Reloaded response code {} in cache", respCode.getCode());
        } catch (DataAccessException ex) {
            log.error("Failed to reload response code {} in Redis", respCode.getCode(), ex);
        }
    }

    public String getRespMessage(final String code) {
        try {
            ResponseCodeDto dto = hashOperations.get(key, code);
            return dto != null ? dto.getMessage() : null;
        } catch (DataAccessException ex) {
            log.error("Failed to fetch response message for code {} from Redis", code, ex);
            return null;
        }
    }
}
