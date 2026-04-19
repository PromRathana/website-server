package com.cambofreelance.websiteservice.model.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class ArticleResponse {
    private UUID id;
    private String title;
    private String content;
    private String status;
    private String categoryName;
    private LocalDateTime createdAt;
}