package com.cambofreelance.websiteservice.model.dto.request;

import lombok.Getter;
import lombok.Setter;
import org.junit.internal.requests.FilterRequest;

import java.util.List;

@Setter
@Getter
public class BaseRequest {

    private String userId;
    private String deviceId;
    private String osType;
    private String osVersion;
    private String ip;
    private String lang;
    private String clientSecret;
    private String clientId;
    private String sessionId;
    private String application;
    private List<FilterRequest> filter;
    private PaginationRequest paginate = new PaginationRequest();
    private String sortBy;
    private String sortDirection;
    private String search;
    private String applicationFeatureId;
    private String applicationFeatureDetailId;
}
