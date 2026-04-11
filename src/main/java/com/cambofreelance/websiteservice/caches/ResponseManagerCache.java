package com.cambofreelance.websiteservice.caches;


import com.cambofreelance.websiteservice.logger.dto.ResponseCodeDto;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class ResponseManagerCache {

    private static final ConcurrentHashMap<String, ResponseCodeDto> responseCodes = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, String> generalCaches = new ConcurrentHashMap<>();

    private ResponseManagerCache() {
        // prevent instantiation
    }

    public static void initRespCodeCache(List<ResponseCodeDto> respCodes) {
        if (respCodes != null && !respCodes.isEmpty()) {
            responseCodes.clear();
            respCodes.forEach(value -> responseCodes.put(value.getCode(), value));
        }
    }

    public static ResponseCodeDto getRespCode(String code) {
        return responseCodes.get(code);
    }

    public static String get(String key) {
        return generalCaches.get(key);
    }

    public static void add(String key, String value) {
        generalCaches.put(key, value);
    }
}
