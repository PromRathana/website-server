package com.cambofreelance.websiteservice.logger.utils;


import com.cambofreelance.websiteservice.logger.contants.ErrorCode;
import com.cambofreelance.websiteservice.logger.exceptions.AppException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.ClientResponse;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Slf4j
public class WebClientLogger {

    private static final Duration TIMEOUT = Duration.ofSeconds(10);

    public static <T> Mono<T> handleResponse(ClientResponse response, Class<T> clazz,
        String action) {
        log.info("📥 [{}] Response Status: {}", action, response.statusCode());

        if (response.statusCode().is2xxSuccessful()) {
            return response.bodyToMono(clazz)
                .doOnNext(body -> log.info("✅ [{}] Response Body: {}", action, body));
        } else {
            return response.bodyToMono(String.class)
                .flatMap(errorBody -> {
                    log.error("❌ [{}] Error Body: {}", action, errorBody);
                    return Mono.error(new AppException(ErrorCode.GENERAL_ERROR));
                });
        }
    }

    public static <T> Mono<T> handleVoidResponse(ClientResponse response, String action) {
        log.info("📥 [{}] Response Status: {}", action, response.statusCode());

        if (response.statusCode().is2xxSuccessful()) {
            log.info("✅ [{}] Success (Void Response)", action);
            return Mono.empty();
        } else {
            return response.bodyToMono(String.class)
                .flatMap(errorBody -> {
                    log.error("❌ [{}] Error Body: {}", action, errorBody);
                    return Mono.error(new AppException(ErrorCode.GENERAL_ERROR));
                });
        }
    }

    public static <T> Mono<T> withTimeout(Mono<T> mono, String action) {
        return mono
            .timeout(TIMEOUT)
            .doOnError(throwable -> log.error("⏱️ [{}] Timeout or connection error: {}", action,
                throwable.getMessage()));
    }
}
