package com.cambofreelance.websiteservice.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.util.UUID;

@Data
public class ArticleRequest {
    @NotBlank(message = "Title is required")
    private String title;
    private String content;
    private String status;
    private UUID categoryId;
}