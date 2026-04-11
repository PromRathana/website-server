package com.cambofreelance.websiteservice.logger.exceptions;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ServiceUnavailableException extends BaseException {

    private final String errorCode;

    public ServiceUnavailableException(Throwable throwable) {
        super(throwable);
        this.errorCode = null;
    }

    public ServiceUnavailableException(Exception exception) {
        super(exception);
        this.errorCode = null;
    }

    public ServiceUnavailableException(String message) {
        super(message);
        this.errorCode = null;
    }

    public ServiceUnavailableException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public ServiceUnavailableException(String errorCode, Exception exception) {
        super(exception);
        this.errorCode = errorCode;
    }
}
