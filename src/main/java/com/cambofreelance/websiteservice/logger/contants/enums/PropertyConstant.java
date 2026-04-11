package com.cambofreelance.websiteservice.logger.contants.enums;

import com.cambofreelance.websiteservice.logger.configs.ProjectConfig;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

public enum PropertyConstant {
    WING_LOGGER_ENABLE("applogger.enabled", "applogger.enabled"),
    WING_LOGGER_MODE("applogger.mode", "applogger.mode");

    private final String key;
    private String description;

    PropertyConstant(String key) {
        this.key = key;
    }

    PropertyConstant(String key, String description) {
        this.key = key;
        this.description = description;
    }

    public static PropertyConstant fromKey(String key) {
        if (StringUtils.isEmpty(key)) {
            return null;
        }

        return Arrays.stream(PropertyConstant.values()).filter(i -> i.key.equals(key)).findFirst()
            .orElse(null);
    }

    public static PropertyConstant fromKey(String key, PropertyConstant defaultValue) {
        if (StringUtils.isEmpty(key)) {
            return defaultValue;
        }

        return Arrays.stream(PropertyConstant.values()).filter(i -> i.key.equals(key)).findFirst()
            .orElse(defaultValue);
    }

    public String getValue() {
        if (StringUtils.isEmpty(this.key)) {
            return null;
        }
        return ProjectConfig.getProperty(this.key);
    }

    public <T> T getValue(Class<T> type) {
        if (StringUtils.isEmpty(this.key)) {
            return null;
        }
        return ProjectConfig.getProperty(this.key, type);
    }
}
