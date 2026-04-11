package com.cambofreelance.websiteservice.utils;

import com.cambofreelance.websiteservice.model.dto.base.ApiBaseResponse;

import java.time.Instant;

public class ResponseUtil {

    public static <T> ApiBaseResponse<T> success(T data, String traceId) {
        return ApiBaseResponse.<T>builder()
            .code("SUC-00001")
            .message("Get data successfully")
            .success(true)
            .timestamp(Instant.now().toEpochMilli())
            .traceId(traceId)
            .data(data)
            .build();
    }

    public static <T> ApiBaseResponse<T> error(String code, String message, String traceId) {
        return ApiBaseResponse.<T>builder()
            .code(code)
            .message(message)
            .success(false)
            .timestamp(Instant.now().toEpochMilli())
            .traceId(traceId)
            .data(null)
            .build();
    }
}