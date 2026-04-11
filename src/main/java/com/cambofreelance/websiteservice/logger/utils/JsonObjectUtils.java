package com.cambofreelance.websiteservice.logger.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@Slf4j
public class JsonObjectUtils {

    private static ObjectMapper objectMapper;

    @Autowired
    public JsonObjectUtils(ObjectMapper objectMapper) {
        JsonObjectUtils.objectMapper = objectMapper;
    }

    public static String toJsonString(Object object) {
        try {
            if (Objects.isNull(object)) {
                return "";
            }
            return objectMapper.writeValueAsString(object);
        } catch (Exception e) {
            return "";
        }
    }

    public static String mapFromHashMapToQueryParamString(Map<String, Object> params) {
        try {
            if (CollectionUtils.isEmpty(params)) {
                return "";
            }

            return params.entrySet().stream().map(entry -> {
                try {
                    String encodedKey = URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8);
                    String encodedValue = URLEncoder.encode((String) entry.getValue(),
                        StandardCharsets.UTF_8);
                    return encodedKey + "=" + encodedValue;
                } catch (Exception e) {
                    return "";
                }
            }).collect(Collectors.joining("&"));
        } catch (Exception e) {
            return "";
        }
    }

    public static String extractJsonFieldByName(String errorBody, String fieldName) {
        if (StringUtils.isEmpty(fieldName)
            || StringUtils.isEmpty(errorBody)) {
            return "";
        }

        try {
            String field = StringUtils.isEmpty(fieldName) ? "error_code" : fieldName;

            JsonNode jsonNode = new ObjectMapper().readTree(errorBody);
            return jsonNode.path(field).asText("");
        } catch (JsonProcessingException e) {
            return "";
        }
    }

    public static String extractErrorCode(String errorBody) {
        String errorCode = extractJsonFieldByName(errorBody, "error_code");
        if (StringUtils.isEmpty(errorCode)) {
            errorCode = extractJsonFieldByName(errorBody, "code");
        }

        return errorCode;
    }

    // get success attribute as boolean
    public static boolean extractSuccessAttribute(String errorBody) {
        try {
            JsonNode jsonNode = new ObjectMapper().readTree(errorBody);
            return jsonNode.path("success").asBoolean(false);
        } catch (JsonProcessingException e) {
            return false;
        }
    }


    public static <Response> Response convertStringToObject(String strData, Class<Response> clazz) {
        try {
            return objectMapper.readValue(strData, clazz);
        } catch (JsonProcessingException e) {
            log.error("Failed to deserialize data for {}", strData, e);
            throw new RuntimeException("Deserialization failed for path.", e);
        }
    }

    public static <Response> Response convertStringToObject(String strData,
        ParameterizedTypeReference<Response> parameters) {
        try {
            return objectMapper.readValue(strData,
                objectMapper.constructType(parameters.getType()));
        } catch (JsonProcessingException e) {
            log.error("Failed to deserialize data for {}", strData, e);
            throw new RuntimeException("Deserialization failed for path.", e);
        }
    }
}
