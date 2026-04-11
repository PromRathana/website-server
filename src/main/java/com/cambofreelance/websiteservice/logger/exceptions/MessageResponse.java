package com.cambofreelance.websiteservice.logger.exceptions;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class MessageResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 2405172041950251807L;
    private String code;
    private String message;
    private transient Object data;
    private boolean success = true;
    private long timestamp;
    private String traceId;

    public MessageResponse(Object data, String errorCode) {
        this.code = errorCode;
        this.data = data;
    }

}

