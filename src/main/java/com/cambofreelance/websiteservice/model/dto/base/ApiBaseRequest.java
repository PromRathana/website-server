package com.cambofreelance.websiteservice.model.dto.base;

import lombok.Data;

import java.util.List;

@Data
public class ApiBaseRequest {

    private List<Filter> filter;
    private String search;
    private String sortBy;
    private String sortDirection;
    private Pagination paginate;

    @Data
    public static class Filter {
        private String field;
        private String operator; // =, in, like, >, <
        private Object value;
    }

    @Data
    public static class Pagination {
        private int page = 1;
        private int size = 25;
    }
}