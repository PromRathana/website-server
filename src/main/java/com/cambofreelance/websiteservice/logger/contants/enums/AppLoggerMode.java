package com.cambofreelance.websiteservice.logger.contants.enums;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

@Slf4j
public enum AppLoggerMode {
    FULL("FULL"),
    PARTIAL("PARTIAL"),
    FULL_PARTIAL("FULL_PARTIAL");

    private final String mode;

    AppLoggerMode(String mode) {
        this.mode = mode;
    }

    public static AppLoggerMode fromValue(String mode) {
        if (StringUtils.isEmpty(mode)) {
            return null;
        }

        return Arrays.stream(AppLoggerMode.values()).filter(i -> i.mode.equals(mode)).findFirst()
            .orElse(null);
    }

    public static AppLoggerMode fromValue(String mode, AppLoggerMode type) {
        if (StringUtils.isEmpty(mode)) {
            return type;
        }

        return Arrays.stream(AppLoggerMode.values()).filter(i -> i.mode.equals(mode)).findFirst()
            .orElse(type);
    }

    public static AppLoggerMode getFromProperty() {
        String logModeStr = PropertyConstant.WING_LOGGER_MODE.getValue();
        return AppLoggerMode.fromValue(logModeStr, AppLoggerMode.FULL);
    }
}
