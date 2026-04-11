package com.cambofreelance.websiteservice.model.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaginationRequest {

    private int page = 1;
    private int size = 20;
}
