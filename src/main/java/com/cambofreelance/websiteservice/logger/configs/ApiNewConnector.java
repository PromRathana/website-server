package com.cambofreelance.websiteservice.logger.configs;

import com.cambofreelance.websiteservice.logger.dto.RequestLogger;
import com.cambofreelance.websiteservice.logger.exceptions.AppException;
import com.cambofreelance.websiteservice.logger.utils.ExceptionUtils;
import com.cambofreelance.websiteservice.logger.utils.JsonObjectUtils;
import com.cambofreelance.websiteservice.logger.utils.LoggerUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class ApiNewConnector {

    private static final List<String> unTrackUri = List.of();
    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    // -------------------- MAIN EXECUTOR --------------------

    private <T> T executeRequest(
        HttpMethod method,
        String baseUrl,
        String path,
        Object body,
        Map<String, Object> queryParams,
        Map<String, String> headers,
        MediaType contentType,
        Class<T> clazz
    ) {
        var logger = logRequestTemp(baseUrl, path, queryParams, body, method.name());
        var start = Instant.now();

        WebClient.RequestBodySpec requestSpec = webClient.method(method)
            .uri(baseUrl, uriBuilder -> {
                uriBuilder.path(path);
                if (queryParams != null) {
                    queryParams.forEach(uriBuilder::queryParam);
                }
                return uriBuilder.build();
            })
            .accept(MediaType.APPLICATION_JSON);

        // Add headers
        if (headers != null) {
            headers.forEach(requestSpec::header);
        }

        // Determine body type
        if (body != null && !HttpMethod.GET.equals(method)) {
            if (MediaType.APPLICATION_FORM_URLENCODED.equals(contentType)) {
                requestSpec.contentType(MediaType.APPLICATION_FORM_URLENCODED);
                requestSpec.body(BodyInserters.fromFormData(convertToMultiValueMap(body)));
            } else {
                requestSpec.contentType(MediaType.APPLICATION_JSON);
                requestSpec.bodyValue(body);
            }
        }

        String response = requestSpec.retrieve()
            .onStatus(HttpStatusCode::is4xxClientError, err -> this.handleServerError(err, path))
            .onStatus(HttpStatusCode::is5xxServerError, err -> this.handleServerError(err, path))
            .bodyToMono(String.class)
            .block();

        var end = Instant.now();
        logResponseTemp(logger, LoggerUtils.durationToTimer(start, end), response);

        ExceptionUtils.validateAndThrowError(response);
        return convertStringToObject(response, clazz);
    }

    private <T> T executeRequest(
        HttpMethod method,
        String baseUrl,
        String path,
        Object body,
        Map<String, Object> queryParams,
        Map<String, String> headers,
        MediaType contentType,
        ParameterizedTypeReference<T> typeRef
    ) {
        var logger = logRequestTemp(baseUrl, path, queryParams, body, method.name());
        var start = Instant.now();

        WebClient.RequestBodySpec requestSpec = webClient.method(method)
            .uri(baseUrl, uriBuilder -> {
                uriBuilder.path(path);
                if (queryParams != null) {
                    queryParams.forEach(uriBuilder::queryParam);
                }
                return uriBuilder.build();
            })
            .accept(MediaType.APPLICATION_JSON);

        // Add headers
        if (headers != null) {
            headers.forEach(requestSpec::header);
        }

        // Determine body type
        if (body != null && !HttpMethod.GET.equals(method)) {
            if (MediaType.APPLICATION_FORM_URLENCODED.equals(contentType)) {
                requestSpec.contentType(MediaType.APPLICATION_FORM_URLENCODED);
                requestSpec.body(BodyInserters.fromFormData(convertToMultiValueMap(body)));
            } else {
                requestSpec.contentType(MediaType.APPLICATION_JSON);
                requestSpec.bodyValue(body);
            }
        }

        String response = requestSpec.retrieve()
            .onStatus(HttpStatusCode::is4xxClientError, err -> this.handleServerError(err, path))
            .onStatus(HttpStatusCode::is5xxServerError, err -> this.handleServerError(err, path))
            .bodyToMono(String.class)
            .block();

        var end = Instant.now();
        logResponseTemp(logger, LoggerUtils.durationToTimer(start, end), response);

        ExceptionUtils.validateAndThrowError(response);
        return convertStringToObject(response, typeRef);
    }

    // -------------------- PUBLIC METHODS --------------------

    public <R> R get(String baseUrl, String path, Class<R> clazz) {
        return executeRequest(HttpMethod.GET, baseUrl, path, null, null, null,
            MediaType.APPLICATION_JSON, clazz);
    }

    public <R> R get(String baseUrl, String path, Class<R> clazz, Map<String, Object> queryParams) {
        return executeRequest(HttpMethod.GET, baseUrl, path, null, queryParams, null,
            MediaType.APPLICATION_JSON, clazz);
    }

    public <R> R postJson(String baseUrl, String path, Object body, Class<R> clazz) {
        return executeRequest(HttpMethod.POST, baseUrl, path, body, null, null,
            MediaType.APPLICATION_JSON, clazz);
    }

    public <R> R postForm(String baseUrl, String path, Map<String, String> formData,
        Class<R> clazz) {
        return executeRequest(HttpMethod.POST, baseUrl, path, formData, null, null,
            MediaType.APPLICATION_FORM_URLENCODED, clazz);
    }

    public <R> R postWithHeader(String baseUrl, String path, Object body, Class<R> clazz,
        Map<String, String> headers) {
        return executeRequest(HttpMethod.POST, baseUrl, path, body, null, headers,
            MediaType.APPLICATION_JSON, clazz);
    }

    public <R> R putJson(String baseUrl, String path, Object body, Class<R> clazz) {
        return executeRequest(HttpMethod.PUT, baseUrl, path, body, null, null,
            MediaType.APPLICATION_JSON, clazz);
    }

    public <R> R delete(String baseUrl, String path, Class<R> clazz) {
        return executeRequest(HttpMethod.DELETE, baseUrl, path, null, null, null,
            MediaType.APPLICATION_JSON, clazz);
    }

    // -------------------- FORM DATA CONVERTER --------------------

    private MultiValueMap<String, String> convertToMultiValueMap(Object body) {
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();

        if (body instanceof Map<?, ?> m) {
            m.forEach((k, v) -> map.add(String.valueOf(k), encode(v)));
        } else {
            // Convert object to map
            Map<String, Object> objMap = objectMapper.convertValue(body, Map.class);
            objMap.forEach((k, v) -> map.add(k, encode(v)));
        }
        return map;
    }

    private String encode(Object value) {
        return URLEncoder.encode(Objects.toString(value, ""), StandardCharsets.UTF_8);
    }

    // -------------------- ERROR HANDLING --------------------

    private Mono<Throwable> handleServerError(ClientResponse response, String path) {
        return response.bodyToMono(String.class).flatMap(errorBody -> {
            String errorCode = JsonObjectUtils.extractErrorCode(errorBody);
            String devMessage = JsonObjectUtils.extractJsonFieldByName(errorBody, "dev_message");
            logResponse(errorBody, path);
            if (StringUtils.isNotBlank(errorCode)) {
                return Mono.error(new AppException(errorCode, devMessage));
            }
            return response.createException();
        });
    }

    // -------------------- CONVERTERS --------------------

    private <R> R convertStringToObject(String strData, Class<R> clazz) {
        try {
            return objectMapper.readValue(strData, clazz);
        } catch (JsonProcessingException e) {
            log.error("Failed to deserialize response: {}", strData, e);
            throw new RuntimeException("Deserialization failed for response.", e);
        }
    }

    private <R> R convertStringToObject(String strData, ParameterizedTypeReference<R> typeRef) {
        try {
            return objectMapper.readValue(strData, objectMapper.constructType(typeRef.getType()));
        } catch (JsonProcessingException e) {
            log.error("Failed to deserialize response: {}", strData, e);
            throw new RuntimeException("Deserialization failed for response.", e);
        }
    }

    // -------------------- LOGGING --------------------

    private RequestLogger logRequestTemp(String baseUrl, String path, Map<String, Object> param,
        Object request, String httpMethod) {
        var logger = new RequestLogger();
        logger.setHttpMethod(httpMethod);

        if (isLogData(path)) {
            String paramStr = Objects.nonNull(param)
                ? LoggerUtils.mapFromHashMapToQueryParamString(param)
                : "";
            String fullPath = StringUtils.defaultIfEmpty(baseUrl, "")
                .concat(StringUtils.defaultIfEmpty(path, ""));
            logger.setPath(fullPath + (StringUtils.isEmpty(paramStr) ? "" : "?" + paramStr));
            if (Objects.nonNull(request)) {
                logger.setRequest(request);
            }
            log.info("➡️ Request: {}", logger);
        }

        return logger;
    }

    private void logResponseTemp(RequestLogger logger, String duration, Object response) {
        logger.setDuration(duration);
        if (Objects.nonNull(response)) {
            logger.setResponse(response);
            log.info("⬅️ Response: {}", logger);
        }
    }

    private void logResponse(Object response, String label) {
        log.warn("[Error Response: {}] {}", label, JsonObjectUtils.toJsonString(response));
    }

    private boolean isLogData(String path) {
        return unTrackUri.stream().noneMatch(uri -> StringUtils.equals(uri, path));
    }

    public <R> R postJson( String baseUrl, String path, Object body, ParameterizedTypeReference<R> typeRef ) { return executeRequest( HttpMethod.POST, baseUrl, path, body, null, null, MediaType.APPLICATION_JSON, typeRef ); }
}
