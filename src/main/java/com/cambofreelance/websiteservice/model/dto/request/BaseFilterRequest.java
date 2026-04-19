package com.cambofreelance.websiteservice.model.dto.request;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class BaseFilterRequest {
    private String userId;
    private String sessionId;
    private String deviceId;
}
