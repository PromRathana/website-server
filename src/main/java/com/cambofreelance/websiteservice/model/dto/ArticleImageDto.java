package com.cambofreelance.websiteservice.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticleImageDto {
    private String articleImageId;
    private String fileName;
    private String imagePath;
    private String imageType;
    private String status;
}