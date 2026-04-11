package com.cambofreelance.websiteservice.logger.contants;

import lombok.Getter;

@Getter
public enum WhiteListHeader {
    APP_LANGUAGE("App-Language", false),
    REQUEST_IDENTIFIER("Request-Identifier", false),
    USER_SESSION("User-Session", false),
    PLATFORM("Platform", false),
    APPLICATION_ID("Application-Id", false),
    APP_NAME("App-Name", false),
    APP_VERSION("App-Version", false),
    USERNAME("Username", false),
    MASTER_ACCOUNT_ID("Master-Account-Id", false),
    LATITUDE("Latitude", true),
    LONGITUDE("Longitude", true),
    DEVICE_ID("Device-Id", false),
    DEVICE_BRAND("Device-Brand", false),
    OS_VERSION("OS-Version", false),
    IP_ADDRESS("Ip-Address", false);

    private final String key;
    private final boolean encrypt;

    WhiteListHeader(String key, boolean encrypt) {
        this.key = key;
        this.encrypt = encrypt;
    }

}