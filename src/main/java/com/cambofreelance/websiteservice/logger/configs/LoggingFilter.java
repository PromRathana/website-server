package com.cambofreelance.websiteservice.logger.configs;

import com.cambofreelance.websiteservice.logger.contants.enums.AppLoggerMode;
import com.cambofreelance.websiteservice.logger.dto.AppLogger;
import com.cambofreelance.websiteservice.logger.utils.LoggerUtils;
import io.opentelemetry.api.trace.Span;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "applogger.enabled", havingValue = "true")
public class LoggingFilter extends OncePerRequestFilter {

    private static final int MAX_LOG_SIZE = 2048;

    private final AppLogger appLogger;

    private String getStringValue(byte[] contentAsByteArray, String characterEncoding) {
        if (contentAsByteArray == null || contentAsByteArray.length == 0) {
            return "";
        }
        try {
            return new String(
                    contentAsByteArray,
                    characterEncoding != null
                            ? java.nio.charset.Charset.forName(characterEncoding)
                            : StandardCharsets.UTF_8
            );
        } catch (Exception e) {
            appLogger.setParam("Error in LoggingFilter.getStringValue()", e.getMessage());
            return new String(contentAsByteArray, StandardCharsets.UTF_8);
        }
    }

    private String truncate(String str) {
        if (str == null) {
            return "";
        }
        return str.length() > MAX_LOG_SIZE ? str.substring(0, MAX_LOG_SIZE) + "...[truncated]"
            : str;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
        @NonNull HttpServletResponse response,
        @NonNull FilterChain filterChain)
        throws ServletException, IOException {

        RequestWrapper requestWrapper = new RequestWrapper(request);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);

        String uri = request.getRequestURI();
        List<String> unTrackUri = Arrays.asList(
            "/actuator/health/readiness",
            "/actuator/health/liveness"
        );

        Instant start = Instant.now();

        try {
            String requestBody = truncate(StringUtils.trim(requestWrapper.getBody()));

            appLogger.setUrl(request.getRequestURI());
            appLogger.setHttpMethod(request.getMethod());
            appLogger.setRequest(LoggerUtils.maskSensitiveData(requestBody));
            appLogger.setServiceName(LoggerUtils.getServiceName(request));
            appLogger.setRemoteAddress(request.getRemoteAddr());

            // Bind trace/span IDs into MDC
            Span currentSpan = Span.current();
            if (currentSpan != null && currentSpan.getSpanContext().isValid()) {
                MDC.put("traceId", currentSpan.getSpanContext().getTraceId());
                MDC.put("spanId", currentSpan.getSpanContext().getSpanId());
            }

            filterChain.doFilter(requestWrapper, responseWrapper);

        } catch (Exception ex) {
            appLogger.setException(ex);
            throw ex;

        } finally {
            Instant end = Instant.now();

            String responseBody = truncate(getStringValue(
                responseWrapper.getContentAsByteArray(),
                response.getCharacterEncoding()
            ));

            appLogger.setResponse(LoggerUtils.maskSensitiveData(responseBody));
            appLogger.setDuration(LoggerUtils.durationToTimer(start, end));

            if (unTrackUri.stream().noneMatch(uri::equals)) {
                switch (AppLoggerMode.getFromProperty()) {
                    case FULL, PARTIAL -> LoggerUtils.logRequestResponse(appLogger);
                    case FULL_PARTIAL -> LoggerUtils.logResponse(appLogger);
                    default -> log.debug("AppLoggerMode not configured, skipping logs.");
                }
            }

            responseWrapper.copyBodyToResponse();

            // Cleanup MDC to avoid leaking across threads
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
    }
}
