package com.cambofreelance.websiteservice.model.mapper.localized;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LocalizedFieldConfig {

    private String enField;
    private String kmField;
    private String thField;
    private String vnField;
}