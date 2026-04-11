package com.cambofreelance.websiteservice.model.dto.base;

import lombok.Data;

import java.util.List;

@Data
public class ApiPageResponse<T> {

    private List<T> content;
    private Metadata metadata;

    @Data
    public static class Metadata {
        private int page;
        private int size;
        private long totalElements;
        private int totalPages;
        private boolean first;
        private boolean last;
        private boolean hasPrevious;
        private boolean hasNext;
    }
}