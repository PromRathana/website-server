package com.cambofreelance.websiteservice.logger.dto;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BaseResponse<T> {

    private boolean success;
    private long timestamp;
    private String code;
    private String message;
    private T data;
    private String traceId;
}
