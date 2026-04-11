package com.cambofreelance.websiteservice.logger.contants;

public final class LoggerErrorCode {

    public static final String DATABASE_CONSTRAINT_ERROR = "DATABASE_CONSTRAINT_ERROR";
    public static final String DATABASE_ERROR = "DATABASE_CONSTRAINT_ERROR";
    public static String INTERNAL_SERVER_ERROR = "ERR-00500";
    public static String SERVICE_NOT_UNAVAILABLE = "ERR-00503";
    public static String DATABASE_CONNECTION_ERROR = "ERR-00502";
    public static String WEB_CLIENT_REQUEST_ERROR = "ERR-00408";
    public static String BAD_REQUEST = "ERR-00400";
    public static String INVALID_FIELD = "ERR_00033";
    private LoggerErrorCode() {
    }
}
