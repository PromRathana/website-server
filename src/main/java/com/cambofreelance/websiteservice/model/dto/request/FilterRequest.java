package com.cambofreelance.websiteservice.model.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FilterRequest {

    private String field;
    private String operator;
    private Object value;
}

