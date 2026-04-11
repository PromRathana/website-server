package com.cambofreelance.websiteservice.logger.contants.enums;

import com.cambofreelance.websiteservice.logger.contants.LoggerConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

@Slf4j
public enum AcceptLanguage {
    KM("km", LoggerConstant.MESSAGE_KH),
    EN("en", LoggerConstant.MESSAGE),
    CN("cn", LoggerConstant.MESSAGE_CN);

    private final String lang;
    private final String key;

    AcceptLanguage(String lang, String key) {
        this.lang = lang;
        this.key = key;
    }

    public static AcceptLanguage fromLang(String lang) {
        if (StringUtils.isEmpty(lang)) {
            return null;
        }

        return Arrays.stream(AcceptLanguage.values()).filter(i -> i.lang.equals(lang)).findFirst()
            .orElse(null);
    }

    public static AcceptLanguage fromValue(String lang, AcceptLanguage type) {
        if (StringUtils.isEmpty(lang)) {
            return type;
        }

        return Arrays.stream(AcceptLanguage.values()).filter(i -> i.lang.equals(lang)).findFirst()
            .orElse(type);
    }

    public String getKey() {
        return this.key;
    }
}
