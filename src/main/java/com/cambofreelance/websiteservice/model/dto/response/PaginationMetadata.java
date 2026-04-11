package com.cambofreelance.websiteservice.model.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaginationMetadata {

    private int page = 1;
    private int size = 25;
    private long totalElements = 0;
    private int totalPages = 0;
    private boolean first;
    private boolean last;
    private boolean hasPrevious;
    private boolean hasNext;
}

