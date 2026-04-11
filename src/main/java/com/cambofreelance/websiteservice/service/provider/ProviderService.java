package com.cambofreelance.websiteservice.service.provider;

import com.cambofreelance.websiteservice.model.dto.base.ApiBaseRequest;
import com.cambofreelance.websiteservice.model.dto.base.ApiBaseResponse;
import com.cambofreelance.websiteservice.model.dto.base.ApiPageResponse;
import com.cambofreelance.websiteservice.model.dto.users.UserDto;

public interface ProviderService {
    ApiBaseResponse<ApiPageResponse<UserDto>> getUsers(ApiBaseRequest request);
}
