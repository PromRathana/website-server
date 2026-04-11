package com.cambofreelance.websiteservice.logger.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ErrorResponse {

    @JsonProperty("messageKm")
    private String messageKm;

    @JsonProperty("messageEn")
    private String messageEn;

    @JsonProperty("messageCh")
    private String messageCh;

    @JsonProperty("httpStatus")
    private String httpStatus;


}
