package com.cambofreelance.websiteservice.model.dto.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LocalizedTextDto {

    private String en;
    private String km;
    private String th;
    private String vn;
}