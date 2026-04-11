package com.cambofreelance.websiteservice.model.dto.base;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ApiBaseResponse<T> {

    private String code;
    private String message;
    private boolean success;
    private long timestamp;
    private String traceId;
    private T data;

}