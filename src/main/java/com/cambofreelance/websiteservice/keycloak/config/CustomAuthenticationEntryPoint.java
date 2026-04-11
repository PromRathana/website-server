package com.cambofreelance.websiteservice.keycloak.config;

import com.cambofreelance.websiteservice.logger.contants.ErrorCode;
import com.cambofreelance.websiteservice.logger.dto.BaseResponse;
import com.cambofreelance.websiteservice.logger.dto.ResponseCodeDto;
import com.cambofreelance.websiteservice.logger.exceptions.MessageResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;
//    private final Tracer tracer;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
        AuthenticationException authException) throws IOException {

        ResponseCodeDto code = new ResponseCodeDto();
        code.setCode(ErrorCode.UNAUTHORIZED);
        code.setMessage("Un Authorization: please help login to authenticate");
        MessageResponse body = new MessageResponse();

        body.setSuccess(false);
        body.setTimestamp(System.currentTimeMillis());
        body.setTraceId(UUID.randomUUID().toString());

        var baseResponse = BaseResponse.<MessageResponse>builder()
            .success(false)
            .timestamp(System.currentTimeMillis())
            .code(code.getCode())
            .message(code.getMessage())
            .data(null)
            .traceId(UUID.randomUUID().toString())
            .build();

        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write(objectMapper.writeValueAsString(baseResponse));
    }

}