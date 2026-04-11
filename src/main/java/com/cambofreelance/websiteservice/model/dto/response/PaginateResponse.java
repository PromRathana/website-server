package com.cambofreelance.websiteservice.model.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PaginateResponse<T> {

    private List<T> content;
    private PaginationMetadata metadata;
}
