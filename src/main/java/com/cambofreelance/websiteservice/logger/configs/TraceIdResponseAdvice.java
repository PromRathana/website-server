package com.cambofreelance.websiteservice.logger.configs;

import com.cambofreelance.websiteservice.caches.ResponseCodeRedisCache;
import com.cambofreelance.websiteservice.caches.ResponseManagerCache;
import com.cambofreelance.websiteservice.logger.contants.Constants;
import com.cambofreelance.websiteservice.logger.contants.enums.AcceptLanguage;
import com.cambofreelance.websiteservice.logger.dto.BaseResponse;
import com.cambofreelance.websiteservice.logger.exceptions.MessageResponse;
import io.opentelemetry.api.trace.Span;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@ControllerAdvice
@RequiredArgsConstructor
public class TraceIdResponseAdvice implements ResponseBodyAdvice<Object> {

    private final ResponseCodeRedisCache responseCodeRedisCache;

    @Override
    public boolean supports(@NonNull MethodParameter returnType,
        @NonNull Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body,
        @NonNull MethodParameter returnType,
        @NonNull MediaType selectedContentType,
        @NonNull Class<? extends HttpMessageConverter<?>> selectedConverterType,
        @NonNull ServerHttpRequest request,
        @NonNull ServerHttpResponse response) {

        String traceId = getCurrentTraceId();
        long timestamp = Instant.now().toEpochMilli();

        if (body instanceof BaseResponse<?> apiResponse) {
            return setTraceAndTimestamp(apiResponse, traceId, timestamp);
        } else if (body instanceof MessageResponse messageResponse) {
            return processMessageResponse(messageResponse, request, traceId, timestamp);
        }

        return body;
    }

    private String getCurrentTraceId() {
        Span span = Span.current();
        if (span != null && span.getSpanContext().isValid()) {
            return span.getSpanContext().getTraceId();
        }
        return UUID.randomUUID().toString();
    }

    private BaseResponse<?> setTraceAndTimestamp(BaseResponse<?> response, String traceId,
        long timestamp) {
        response.setTraceId(traceId);
        response.setTimestamp(timestamp);
        return response;
    }

    private MessageResponse processMessageResponse(MessageResponse messageResponse,
        ServerHttpRequest request,
        String traceId,
        long timestamp) {
        var responseCode = responseCodeRedisCache.getRespCode(messageResponse.getCode());
        if (Objects.isNull(responseCode)) {
            responseCode = ResponseManagerCache.getRespCode(messageResponse.getCode());
        }
        String acceptLangHeader = request.getHeaders().getFirst(Constants.CLIENT_LANG);
        AcceptLanguage lang = AcceptLanguage.fromValue(
            acceptLangHeader != null ? acceptLangHeader : Constants.LANG_EN,
            AcceptLanguage.EN
        );
        switch (lang) {
            case CN -> messageResponse.setMessage(responseCode.getMessageCn());
            case KM -> messageResponse.setMessage(responseCode.getMessageKm());
            default -> messageResponse.setMessage(responseCode.getMessage());
        }

        messageResponse.setTraceId(traceId);
        messageResponse.setTimestamp(timestamp);
        return messageResponse;
    }
}
