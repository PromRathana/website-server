package com.cambofreelance.websiteservice.logger.contants;

public final class ErrorCode {

    public static final String SUCCESS = "SUC-00001";
    public static final String USER_CREATE_SUC = "SUC-00002";
    public static final String USER_UPDATE_SUC = "SUC-00003";
    public static final String USER_DELETE_SUC = "SUC-00004";
    public static final String GENERAL_ERROR = "ERR-00002";
    public static final String INVALID_REQ_ERROR = "ERR-00001";
    public static final String UNAUTHORIZED = "ERR-00003";
    public static final String ACCOUNT_NOT_REQUIRE_CHANGE_PASSWORD = "ERR-00004";
    public static final String USER_ALREADY_EXISTS = "ERR-00005";
    public static final String USER_NOT_FOUND = "ERR-00006";
    public static final String LOGIN_SUCCESS = "ERR-00007";
    public static final String USER_IS_NOT_ACTIVE = "ERR-00008";
    public static final String ACCESS_DENIED = "ERR-00009";
    public static final String USERNAME_ALREADY_EXIST = "ERR-00010";
    public static final String EMAIL_ALREADY_EXIST = "ERR-00011";
    public static final String PHONE_ALREADY_EXIST = "ERR-00012";
    public static final String INCORRECT_USERNAME_PASSWORD = "ERR-00013";
    public static final String CHANGE_PASSWORD_SUCCESS = "SUC-00005";
    public static final String UPDATE_PROFILE_SUCCESS = "SUC-00006";
    public static final String CURRENT_INCORRECT = " ERR-00015";
    public static final String CONFIRM_NOT_MATH = "ERR-00016";
    public static final String SESSION_EXPIRED = "ERR-00017";
    public static final String OTP_NOT_MATCH = "ERR-00018";
    public static final String OTP_LIMIT_PER_DAY = "ERR-00019";

    public static final String DATA_NOT_FOUND = "ERR-00020";

    public static final String APPLICATION_FEATURE_CODE_ALREADY_EXISTS = "AUTH-0001";
    public static final String APPLICATION_FEATURE_DETAIL_CODE_ALREADY_EXISTS = "AUTH-0002";
    public static final String APPLICATION_FEATURE_NOT_FOUND = "AUTH-0003";
    public static final String APPLICATION_FEATURE_DETAIL_NOT_FOUND = "AUTH-0004";
    public static final String CREATE_APPLICATION_FEATURE_SUCCESS = "AUTH-0005";
    public static final String UPDATE_APPLICATION_FEATURE_SUCCESS = "AUTH-0006";
    public static final String DELETE_APPLICATION_FEATURE_SUCCESS = "AUTH-0007";
    public static final String CREATE_APPLICATION_FEATURE_DETAIL_SUCCESS = "AUTH-0008";
    public static final String UPDATE_APPLICATION_FEATURE_DETAIL_SUCCESS = "AUTH-0009";
    public static final String DELETE_APPLICATION_FEATURE_DETAIL_SUCCESS = "AUTH-0010";
    public static final String LOTTERY_RESULT_EXISTED = "LOTTERY-0001";


    private ErrorCode() {
    }
}