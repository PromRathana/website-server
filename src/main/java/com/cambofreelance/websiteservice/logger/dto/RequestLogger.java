package com.cambofreelance.websiteservice.logger.dto;

import com.cambofreelance.websiteservice.logger.utils.LoggerUtils;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RequestLogger {

    String path;
    String request;
    String response;
    String duration;
    String httpMethod;

    public void setRequest(Object request) {
        this.request = LoggerUtils.toJsonString(request);
    }

    public void setResponse(Object response) {
        this.response = LoggerUtils.toJsonString(response);
    }
}
