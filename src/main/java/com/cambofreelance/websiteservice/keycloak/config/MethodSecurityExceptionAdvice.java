package com.cambofreelance.websiteservice.keycloak.config;

import com.cambofreelance.websiteservice.logger.contants.ErrorCode;
import com.cambofreelance.websiteservice.logger.dto.BaseResponse;
import com.cambofreelance.websiteservice.logger.dto.ResponseCodeDto;
import com.cambofreelance.websiteservice.logger.exceptions.MessageResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.UUID;

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class MethodSecurityExceptionAdvice {

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<BaseResponse<MessageResponse>> handleAccessDenied(
        AccessDeniedException ex,
        HttpServletRequest req) {
        var code = new ResponseCodeDto();
        code.setCode(ErrorCode.ACCESS_DENIED);
        code.setMessage("Access Denied");
        code.setDescription(ex.getMessage());

        var baseResponse = BaseResponse.<MessageResponse>builder()
            .success(false)
            .timestamp(System.currentTimeMillis())
            .code(code.getCode())
            .message(code.getMessage())
            .data(null)
            .traceId(UUID.randomUUID().toString())
            .build();

        return new ResponseEntity<>(baseResponse, HttpStatus.FORBIDDEN);
    }

}
