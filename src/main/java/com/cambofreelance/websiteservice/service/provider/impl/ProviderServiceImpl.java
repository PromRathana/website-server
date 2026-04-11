package com.cambofreelance.websiteservice.service.provider.impl;

import com.cambofreelance.websiteservice.logger.configs.ApiNewConnector;
import com.cambofreelance.websiteservice.model.dto.base.ApiBaseRequest;
import com.cambofreelance.websiteservice.model.dto.base.ApiBaseResponse;
import com.cambofreelance.websiteservice.model.dto.base.ApiPageResponse;
import com.cambofreelance.websiteservice.model.dto.users.UserDto;
import com.cambofreelance.websiteservice.service.provider.ProviderService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ProviderServiceImpl implements ProviderService {
    private final ApiNewConnector apiNewConnector;
    @Value("${authentication-service.url}")
    private String authBaseUrl;
    public ApiBaseResponse<ApiPageResponse<UserDto>> getUsers(ApiBaseRequest request) {
        return apiNewConnector.postJson(
            authBaseUrl,
            "/api/internal/users",
            request,
            new ParameterizedTypeReference<>() {
            }
        );
    }

}