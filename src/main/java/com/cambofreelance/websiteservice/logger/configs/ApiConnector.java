package com.cambofreelance.websiteservice.logger.configs;

import com.cambofreelance.websiteservice.logger.contants.Constants;
import com.cambofreelance.websiteservice.logger.dto.RequestLogger;
import com.cambofreelance.websiteservice.logger.exceptions.AppException;
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
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
@Slf4j
@RequiredArgsConstructor
public class ApiConnector {

    private static final List<String> unTrackUri = List.of();
    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    public <Response> Response postWithHeader(String baseUrl, String path, Object data,
        Class<Response> clazz, Map<String, String> headers) {
        RequestLogger logger = logRequestTemp(baseUrl, path, data, Constants.POS_HTTP_METHOD);
        Instant start = Instant.now();

        WebClient.RequestBodySpec requestSpec = webClient.method(HttpMethod.POST)
            .uri(baseUrl + path)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON);

        // Add custom headers
        if (headers != null && !headers.isEmpty()) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                requestSpec = requestSpec.header(entry.getKey(), entry.getValue());
            }
        }

        String response = requestSpec
            .bodyValue(data)
            .retrieve()
            .onStatus(HttpStatusCode::is4xxClientError, err -> this.handleServerError(err, path))
            .onStatus(HttpStatusCode::is5xxServerError, err -> this.handleServerError(err, path))
            .bodyToMono(String.class)
            .block();

        Instant end = Instant.now();
        logResponseTemp(logger, LoggerUtils.durationToTimer(start, end), response);
        return convertStringToObject(response, clazz);
    }

    public <Response> Response post(String baseUrl, String path, Object data,
        Class<Response> clazz) {
        var logger = logRequestTemp(baseUrl, path, data);

        var start = Instant.now();

        var response = webClient.method(HttpMethod.POST).uri(baseUrl + path)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).bodyValue(data).retrieve()
            .onStatus(HttpStatusCode::is4xxClientError, err -> this.handleServerError(err, path))
            .onStatus(HttpStatusCode::is5xxServerError, err -> this.handleServerError(err, path))
            .bodyToMono(String.class).block();

        var end = Instant.now();
        logResponseTemp(logger, LoggerUtils.durationToTimer(start, end), response);
        return convertStringToObject(response, clazz);
    }

    public <Response> Response post(String baseUrl, String path, Object data,
        ParameterizedTypeReference<Response> parameters) {
        var logger = logRequestTemp(baseUrl, path, data);
        var start = Instant.now();
        String response = webClient.method(HttpMethod.POST).uri(baseUrl + path)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).bodyValue(data).retrieve()
            .onStatus(HttpStatusCode::is4xxClientError, err -> this.handleServerError(err, path))
            .onStatus(HttpStatusCode::is5xxServerError, err -> this.handleServerError(err, path))
            .bodyToMono(String.class).block();
        Instant end = Instant.now();
        logResponseTemp(logger, LoggerUtils.durationToTimer(start, end), response);
        return convertStringToObject(response, parameters);
    }

    public <Response> Response get(String baseUrl, String path, Class<Response> clazz,
        Map<String, Object> param) {
        var logger = logRequestTemp(baseUrl, path, param, null, Constants.GET_HTTP_METHOD);
        var start = Instant.now();

        var response = webClient.method(HttpMethod.GET).uri(baseUrl, uriBuilder -> {
                uriBuilder.path(path);
                if (Objects.nonNull(param)) {
                    param.keySet().forEach(key -> uriBuilder.queryParam(key, param.get(key)));
                }
                return uriBuilder.build();
            }).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).retrieve()
            .onStatus(HttpStatusCode::is4xxClientError, err -> this.handleServerError(err, path))
            .onStatus(HttpStatusCode::is5xxServerError, err -> this.handleServerError(err, path))
            .bodyToMono(String.class).block();

        var end = Instant.now();
        logResponseTemp(logger, LoggerUtils.durationToTimer(start, end), response);
        return convertStringToObject(response, clazz);
    }

    public <Response> Response get(String baseUrl, String path, Class<Response> clazz) {
        return get(baseUrl, path, clazz, null);
    }

    public <Response> Response get(String baseUrl, String path,
        ParameterizedTypeReference<Response> parameters,
        Map<String, Object> param) {
        var logger = logRequestTemp(baseUrl, path, param, null, Constants.GET_HTTP_METHOD);
        var start = Instant.now();

        String response = webClient.method(HttpMethod.GET).uri(baseUrl, uriBuilder -> {
                uriBuilder.path(path);
                if (Objects.nonNull(param)) {
                    param.keySet().forEach(key -> uriBuilder.queryParam(key, param.get(key)));
                }
                return uriBuilder.build();
            }).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).retrieve()
            .onStatus(HttpStatusCode::is4xxClientError, err -> this.handleServerError(err, path))
            .onStatus(HttpStatusCode::is5xxServerError, err -> this.handleServerError(err, path))
            .bodyToMono(String.class).block();

        var end = Instant.now();
        logResponseTemp(logger, LoggerUtils.durationToTimer(start, end), response);
        return convertStringToObject(response, parameters);
    }

    public <Response> Response get(String baseUrl, String path,
        ParameterizedTypeReference<Response> parameters) {
        return get(baseUrl, path, parameters, null);
    }

    private void logRequest(String baseUrl, String path, Object request) {
        logRequest(baseUrl, path, null, request);
    }

    private void logRequest(String baseUrl, String path, Map<String, Object> param,
        Object request) {
        if (isLogData(path)) {
            String paramStr =
                Objects.nonNull(param) ? JsonObjectUtils.mapFromHashMapToQueryParamString(param)
                    : "";
            String fullPath = StringUtils.defaultIfEmpty(baseUrl, "")
                .concat(StringUtils.defaultIfEmpty(path, ""));
            String fullUrl = fullPath + (StringUtils.isEmpty(paramStr) ? "" : "?".concat(paramStr));

            log.info("[Request]:{} > {}", fullUrl, JsonObjectUtils.toJsonString(request));
        }
    }

    private void logResponse(Object response) {
        logResponse(response, null);
    }

    private void logResponse(Object response, String label) {
        log.info("[Response]:{} > {}", StringUtils.defaultIfEmpty(label, ""),
            JsonObjectUtils.toJsonString(response));
    }

    private boolean isLogData(String path) {
        return unTrackUri.stream().noneMatch(uri -> StringUtils.equals(uri, path));
    }

    private Mono<Throwable> handleServerError(ClientResponse response, String path) {
        return response.bodyToMono(String.class).flatMap(errorBody -> {
            String errorCode = JsonObjectUtils.extractErrorCode(errorBody);
            String devMessage = JsonObjectUtils.extractJsonFieldByName(errorBody, "dev_message");

            logResponse(errorBody, path);
            if (errorCode != null && !errorCode.isEmpty()) {
                return Mono.error(new AppException(errorCode, devMessage));
            }

            return response.createException();
        });
    }

    private <Response> Response convertStringToObject(String strData, Class<Response> clazz) {
        try {
            return objectMapper.readValue(strData, clazz);
        } catch (JsonProcessingException e) {
            log.error("Failed to deserialize data for {}", strData, e);
            throw new RuntimeException("Deserialization failed for path.", e);
        }
    }

    private <Response> Response convertStringToObject(String strData,
        ParameterizedTypeReference<Response> parameters) {
        try {
            return objectMapper.readValue(strData,
                objectMapper.constructType(parameters.getType()));
        } catch (JsonProcessingException e) {
            log.error("Failed to deserialize data for {}", strData, e);
            throw new RuntimeException("Deserialization failed for path.", e);
        }
    }

    private RequestLogger logRequestTemp(String baseUrl, String path, Object request) {
        return logRequestTemp(baseUrl, path, null, request, Constants.POS_HTTP_METHOD);
    }

    private RequestLogger logRequestTemp(String baseUrl, String path, Object request,
        String httpMethod) {
        return logRequestTemp(baseUrl, path, null, request, httpMethod);
    }


    private RequestLogger logRequestTemp(String baseUrl, String path, Map<String, Object> param,
        Object request, String httpMethod) {
        var logger = new RequestLogger();
        logger.setHttpMethod(httpMethod);

        if (isLogData(path)) {
            String paramStr =
                Objects.nonNull(param) ? LoggerUtils.mapFromHashMapToQueryParamString(param) : "";
            String fullPath = StringUtils.defaultIfEmpty(baseUrl, "")
                .concat(StringUtils.defaultIfEmpty(path, ""));

            logger.setPath(fullPath + (StringUtils.isEmpty(paramStr) ? "" : "?".concat(paramStr)));
            if (Objects.nonNull(request)) {
                logger.setRequest(request);
            }
            log.info(String.valueOf(logger));
        }

        return logger;
    }

    private void logResponseTemp(RequestLogger logger, String duration, Object response) {
        logger.setDuration(duration);
        if (Objects.nonNull(response)) {
            logger.setResponse(response);
            log.info(String.valueOf(logger));
        }
    }
}