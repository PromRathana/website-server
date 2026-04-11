package com.cambofreelance.websiteservice.logger.exceptions;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.http.HttpStatus;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@ToString
public class AppException extends BaseException implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    private HttpStatus httpStatus;
    private String errorCode;
    private String[] params;
    private Object data;
    private boolean isNotice;

    public AppException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public AppException(String errorCode) {
        this.errorCode = errorCode;
    }

    public HttpStatus getHttpStatus() {
        if (this.httpStatus == null) {
            this.httpStatus = HttpStatus.BAD_REQUEST;
        }
        return httpStatus;
    }
}
