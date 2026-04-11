package com.cambofreelance.websiteservice.logger.utils;

import com.cambofreelance.websiteservice.logger.contants.enums.AppLoggerMode;
import com.cambofreelance.websiteservice.logger.contants.enums.PropertyConstant;
import com.cambofreelance.websiteservice.logger.dto.AppLogger;
import com.cambofreelance.websiteservice.logger.dto.RequestLogger;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
@Slf4j
public class LoggerUtils {

    private static ObjectMapper objectMapper;

    @Autowired
    private LoggerUtils(ObjectMapper objectMapper) {
        LoggerUtils.objectMapper = objectMapper;
    }

    public static String getServiceName(HttpServletRequest request) {
        String serviceName = request.getHeader("SERVICE-NAME");
        return ObjectUtils.isEmpty(serviceName) ? "LOTTERY-SERVICE" : serviceName;
    }

    public static String toJsonString(Object object) {
        if (Objects.isNull(object)) {
            return "";
        }
        try {
            return objectMapper.writeValueAsString(object);
        } catch (Exception e) {
            if (e instanceof JsonProcessingException jpe) {
                logJsonError("toJsonString", jpe, null);
            } else {
                log.warn("Error in LoggerUtils.toJsonString: {}", e.getMessage());
            }
            return "";
        }
    }

    public static String maskSensitiveData(String jsonStr) {
        if (StringUtils.isBlank(jsonStr)) {
            return "";
        }
        try {
            JsonNode rootNode = objectMapper.readTree(jsonStr);
            maskSensitiveFields(rootNode);
            return objectMapper.writeValueAsString(rootNode);
        } catch (Exception e) {
            if (e instanceof JsonProcessingException jpe) {
                logJsonError("maskSensitiveData", jpe, jsonStr);
            } else {
                log.warn("Unexpected error in maskSensitiveData: {}", e.getMessage());
            }
            // Fail-safe: return raw string
            return jsonStr;
        }
    }

    private static void maskSensitiveFields(JsonNode node) {
        if (node.isArray()) {
            node.forEach(LoggerUtils::maskSensitiveFields);
            return;
        }
        if (node.isObject()) {
            List<String> sensitiveText = Arrays.asList(
                "pin", "password", "clientSecret", "clientId", "accessToken", "refreshToken",
                "newPassword", "confirmPassword", "oldPassword", "token", "ssn",
                "socialSecurityNumber",
                "creditCard", "cardNumber", "cvv", "cvc", "secret", "auth", "authentication",
                "apikey",
                "apiKey", "api_key", "authorization", "bearer", "type", "privateKey", "private_key"
            );
            ObjectNode objectNode = (ObjectNode) node;
            objectNode.fields().forEachRemaining(entry -> {
                boolean isSensitiveField = extractWords(entry.getKey()).stream()
                    .anyMatch(sensitiveText::contains);
                if (isSensitiveField) {
                    objectNode.put(entry.getKey(), "******");
                }
                maskSensitiveFields(entry.getValue()); // recursive
            });
        }
    }

    // Extract words from CamelCase or snake_case
    private static List<String> extractWords(String input) {
        List<String> words = new ArrayList<>();
        String[] snakeCaseWords = input.split("_");
        Pattern camelCasePattern = Pattern.compile("([A-Z][a-z]*)|([a-z]+)");
        for (String word : snakeCaseWords) {
            Matcher matcher = camelCasePattern.matcher(word);
            while (matcher.find()) {
                words.add(matcher.group().toLowerCase());
            }
        }
        return words;
    }

    public static String mapFromHashMapToQueryParamString(Map<String, Object> params) {
        if (CollectionUtils.isEmpty(params)) {
            return "";
        }
        try {
            return params.entrySet().stream().map(entry -> {
                try {
                    String encodedKey = Objects.nonNull(entry.getKey())
                        ? URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8)
                        : "";
                    String encodedValue = Objects.nonNull(entry.getValue())
                        ? URLEncoder.encode(String.valueOf(entry.getValue()),
                        StandardCharsets.UTF_8)
                        : "";
                    return encodedKey + "=" + encodedValue;
                } catch (Exception e) {
                    log.error("Error encoding param {}: {}", entry.getKey(), e.getMessage());
                    return "";
                }
            }).filter(StringUtils::isNotBlank).collect(Collectors.joining("&"));
        } catch (Exception e) {
            log.warn("Error in mapFromHashMapToQueryParamString: {}", e.getMessage());
            return "";
        }
    }

    public static String durationToTimer(Duration duration) {
        long millis = duration.toMillis();
        if (millis >= 60000) {
            return duration.toMinutes() + " minutes " + (millis % 60000) + " milliseconds";
        } else if (millis >= 1000) {
            return duration.getSeconds() + " seconds " + (millis % 1000) + " milliseconds";
        } else {
            return millis + " milliseconds";
        }
    }

    public static String durationToTimer(Instant start, Instant end) {
        return durationToTimer(Duration.between(start, end));
    }

    public static boolean isWingLoggerEnable() {
        return Optional.ofNullable(PropertyConstant.WING_LOGGER_ENABLE.getValue(Boolean.class))
            .orElse(false);
    }

    public static void logRequestResponse(AppLogger appLogger) {
        if (!isWingLoggerEnable()) {
            return;
        }

        AppLoggerMode logMode = AppLoggerMode.getFromProperty();
        StringBuilder strBuilder = new StringBuilder(
            "\n=============== " + (StringUtils.defaultIfEmpty(appLogger.getHeader(),
                appLogger.getUrl())) + " ================");

        appendIfNotBlank(strBuilder, "# URL                 : ", appLogger.getUrl());
        appendIfNotBlank(strBuilder, "# SERVICE NAME        : ", appLogger.getServiceName());
        appendIfNotBlank(strBuilder, "# REMOTE ADDRESS      : ", appLogger.getRemoteAddress());
        appendIfNotBlank(strBuilder, "# HTTP METHOD         : ", appLogger.getHttpMethod());
        appendIfNotBlank(strBuilder, "# DURATION            : ", appLogger.getDuration());
        appendIfNotNull(strBuilder, "# REQUEST             : ", appLogger.getRequest());

        appLogger.getParams().forEach((k, v) -> strBuilder.append(
            String.format("%n# PARAM               : %s => %s", k, v)));

        appendIfNotNull(strBuilder, "# RESPONSE            : ", appLogger.getResponse());

        if (!CollectionUtils.isEmpty(appLogger.getExternalLog()) && logMode == AppLoggerMode.FULL) {
            AtomicInteger index = new AtomicInteger();
            int size = appLogger.getExternalLog().size();
            appLogger.getExternalLog().forEach(log -> {
                int i = index.incrementAndGet();
                strBuilder.append("\n--------------------------------------------");
                appendIfNotBlank(strBuilder, "# MICRO URL(" + i + ")        : ", log.getPath());
                appendIfNotNull(strBuilder, "# MICRO REQUEST(" + i + ")    : ", log.getRequest());
                appendIfNotNull(strBuilder, "# MICRO RESPONSE(" + i + ")   : ", log.getResponse());
                appendIfNotBlank(strBuilder, "# DURATION(" + i + ")         : ", log.getDuration());
                if (i == size) {
                    strBuilder.append("\n--------------------------------------------");
                }
            });
        }

        if (Objects.nonNull(appLogger.getException())) {
            strBuilder.append("\n").append(ExceptionUtils.getStackTrace(appLogger.getException()));
        }

        strBuilder.append("\n====================================================================");
        log.info(strBuilder.toString());
    }

    public static void logRequest(AppLogger appLogger) {
        if (!isWingLoggerEnable()) {
            return;
        }

        StringBuilder strBuilder = new StringBuilder(
            "\n=============== " + (StringUtils.defaultIfEmpty(appLogger.getHeader(),
                appLogger.getUrl())) + " ================");
        appendIfNotBlank(strBuilder, "# URL                 : ", appLogger.getUrl());
        appendIfNotBlank(strBuilder, "# SERVICE NAME        : ", appLogger.getServiceName());
        appendIfNotBlank(strBuilder, "# REMOTE ADDRESS      : ", appLogger.getRemoteAddress());
        appendIfNotBlank(strBuilder, "# HTTP METHOD         : ", appLogger.getHttpMethod());
        appendIfNotNull(strBuilder, "# REQUEST             : ", appLogger.getRequest());

        strBuilder.append("\n====================================================================");
        log.info(strBuilder.toString());
    }

    public static void logResponse(AppLogger appLogger) {
        if (!isWingLoggerEnable()) {
            return;
        }

        StringBuilder strBuilder = new StringBuilder(
            "\n=============== " + (StringUtils.defaultIfEmpty(appLogger.getHeader(),
                appLogger.getUrl())) + " ================");
        appendIfNotBlank(strBuilder, "# URL                 : ", appLogger.getUrl());
        appendIfNotBlank(strBuilder, "# DURATION            : ", appLogger.getDuration());
        appendIfNotNull(strBuilder, "# RESPONSE            : ", appLogger.getResponse());

        if (Objects.nonNull(appLogger.getException())) {
            strBuilder.append("\n").append(ExceptionUtils.getStackTrace(appLogger.getException()));
        }

        strBuilder.append("\n====================================================================");
        log.info(strBuilder.toString());
    }

    public static void logOutGoingRequestResponse(RequestLogger requestLogger) {
        if (!isWingLoggerEnable()) {
            return;
        }

        StringBuilder strBuilder = new StringBuilder(
            "\n--------------------------------------------");
        appendIfNotBlank(strBuilder, "# MICRO URL        : ", requestLogger.getPath());
        appendIfNotNull(strBuilder, "# MICRO REQUEST    : ", requestLogger.getRequest());
        appendIfNotBlank(strBuilder, "# HTTP METHOD      : ", requestLogger.getHttpMethod());
        appendIfNotNull(strBuilder, "# MICRO RESPONSE   : ", requestLogger.getResponse());
        appendIfNotBlank(strBuilder, "# DURATION         : ", requestLogger.getDuration());
        strBuilder.append("\n--------------------------------------------");
        log.info(strBuilder.toString());
    }

    public static void logOutGoingRequest(RequestLogger requestLogger) {
        if (!isWingLoggerEnable()) {
            return;
        }

        StringBuilder strBuilder = new StringBuilder(
            "\n--------------------------------------------");
        appendIfNotBlank(strBuilder, "# MICRO URL        : ", requestLogger.getPath());
        appendIfNotNull(strBuilder, "# MICRO REQUEST    : ", requestLogger.getRequest());
        appendIfNotBlank(strBuilder, "# HTTP METHOD      : ", requestLogger.getHttpMethod());
        strBuilder.append("\n--------------------------------------------");
        log.info(strBuilder.toString());
    }

    public static void logOutGoingResponse(RequestLogger requestLogger) {
        if (!isWingLoggerEnable()) {
            return;
        }

        StringBuilder strBuilder = new StringBuilder(
            "\n--------------------------------------------");
        appendIfNotBlank(strBuilder, "# MICRO URL        : ", requestLogger.getPath());
        appendIfNotNull(strBuilder, "# MICRO RESPONSE   : ", requestLogger.getResponse());
        appendIfNotBlank(strBuilder, "# DURATION         : ", requestLogger.getDuration());
        strBuilder.append("\n--------------------------------------------");
        log.info(strBuilder.toString());
    }

    // --- helper methods ---
    private static void appendIfNotBlank(StringBuilder sb, String label, String value) {
        if (StringUtils.isNotBlank(value)) {
            sb.append("\n").append(label).append(value);
        }
    }

    private static void appendIfNotNull(StringBuilder sb, String label, Object value) {
        if (Objects.nonNull(value)) {
            sb.append("\n").append(label).append(value);
        }
    }

    // --- enhanced JSON error logger ---
    private static void logJsonError(String context, JsonProcessingException jpe, String jsonStr) {
        int offset = jpe.getLocation() != null ? (int) jpe.getLocation().getCharOffset() : -1;
        int column = jpe.getLocation() != null ? jpe.getLocation().getColumnNr() : -1;

        String snippet = "";
        if (jsonStr != null && offset >= 0 && offset < jsonStr.length()) {
            int start = Math.max(0, offset - 50);
            int end = Math.min(jsonStr.length(), offset + 50);
            snippet = jsonStr.substring(start, end);
        }

        char badChar = (jsonStr != null && offset >= 0 && offset < jsonStr.length())
            ? jsonStr.charAt(offset)
            : '?';

        log.warn("JSON parsing error in {}: {} (col: {}, offset: {}, badChar: '{}', snippet: [{}])",
            context, jpe.getMessage(), column, offset, badChar, snippet);
    }
}
