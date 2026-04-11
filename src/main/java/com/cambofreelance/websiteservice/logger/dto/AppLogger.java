package com.cambofreelance.websiteservice.logger.dto;

import com.cambofreelance.websiteservice.logger.contants.enums.AppLoggerMode;
import com.cambofreelance.websiteservice.logger.utils.LoggerUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.context.WebApplicationContext;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Getter
@Setter
@Component
@Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
@Slf4j
public class AppLogger {

    private String url;
    private String httpMethod;
    private String serviceName;
    private String remoteAddress;
    private String request;
    private String response;
    private String duration;
    private Exception exception;
    private Map<String, String> params = new HashMap<>();
    private String header;
    private List<RequestLogger> externalLog = new ArrayList<>();

    private String contentType;
    private String acceptLanguage;
    private String deviceId;
    private String applicationId;
    private String osVersion;
    private String osType;
    private String appVersion;
    private String userId;

    public void setParam(String key, String value) {
        params.put(key, value);
    }

    public void addExternalLog(RequestLogger requestLogger) {
        AppLoggerMode logMode = AppLoggerMode.getFromProperty();
        if (logMode == AppLoggerMode.FULL_PARTIAL) {
            LoggerUtils.logOutGoingRequest(requestLogger);
        }
        externalLog.add(requestLogger);
    }

    public void logExternalResponse(RequestLogger requestLogger) {
        AppLoggerMode logMode = AppLoggerMode.getFromProperty();
        switch (logMode) {
            case PARTIAL -> LoggerUtils.logOutGoingRequestResponse(requestLogger);
            case FULL_PARTIAL -> LoggerUtils.logOutGoingResponse(requestLogger);
        }
    }

    public void setHeadersFromMap(Map<String, String> headers) {
        this.contentType = headers.get("Content-Type");
        this.acceptLanguage = headers.get("Accept-Language");
        this.deviceId = headers.get("Device-Id");
        this.applicationId = headers.get("Application-Id");
        this.osVersion = headers.get("Os-Version");
        this.osType = headers.get("Os-Type");
        this.appVersion = headers.get("App-Version");
        this.userId = headers.get("User-Id");
    }

    @Override
    public String toString() {
        AppLoggerMode logMode = AppLoggerMode.getFromProperty();

        String logHeader = StringUtils.isNotEmpty(header) ? header : url;
        StringBuilder strBuilder = new StringBuilder(
            "\n=============== " + logHeader + " ================");

        if (StringUtils.isNotEmpty(url)) {
            strBuilder.append("\n# URL                 : ").append(url);
        }
        if (StringUtils.isNotEmpty(serviceName)) {
            strBuilder.append("\n# SERVICE NAME        : ").append(serviceName);
        }
        if (StringUtils.isNotEmpty(remoteAddress)) {
            strBuilder.append("\n# REMOTE ADDRESS      : ").append(remoteAddress);
        }
        if (StringUtils.isNotEmpty(httpMethod)) {
            strBuilder.append("\n# HTTP METHOD         : ").append(httpMethod);
        }
        if (StringUtils.isNotEmpty(duration)) {
            strBuilder.append("\n# DURATION            : ").append(duration);
        }
        if (Objects.nonNull(request)) {
            strBuilder.append("\n# REQUEST             : ").append(request);
        }

        // Log custom headers
        if (StringUtils.isNotEmpty(contentType)) {
            strBuilder.append("\n# Content-Type        : ").append(contentType);
        }
        if (StringUtils.isNotEmpty(acceptLanguage)) {
            strBuilder.append("\n# Accept-Language     : ").append(acceptLanguage);
        }
        if (StringUtils.isNotEmpty(deviceId)) {
            strBuilder.append("\n# Device-Id           : ").append(deviceId);
        }
        if (StringUtils.isNotEmpty(applicationId)) {
            strBuilder.append("\n# Application-Id      : ").append(applicationId);
        }
        if (StringUtils.isNotEmpty(osVersion)) {
            strBuilder.append("\n# Os-Version          : ").append(osVersion);
        }
        if (StringUtils.isNotEmpty(osType)) {
            strBuilder.append("\n# Os-Type             : ").append(osType);
        }
        if (StringUtils.isNotEmpty(appVersion)) {
            strBuilder.append("\n# App-Version         : ").append(appVersion);
        }
        if (StringUtils.isNotEmpty(userId)) {
            strBuilder.append("\n# User-Id             : ").append(userId);
        }

        params.forEach(
            (k, v) -> strBuilder.append(String.format("%n# PARAM               : %s => %s", k, v)));

        if (Objects.nonNull(response)) {
            strBuilder.append("\n# RESPONSE            : ").append(response);
        }

        if (!CollectionUtils.isEmpty(externalLog) && logMode == AppLoggerMode.FULL) {
            AtomicInteger index = new AtomicInteger();
            int size = externalLog.size();
            externalLog.forEach(log -> {
                int i = index.incrementAndGet();
                strBuilder.append("\n--------------------------------------------");
                if (StringUtils.isNotEmpty(log.getPath())) {
                    strBuilder.append("\n# MICRO URL(").append(i).append(")         : ")
                        .append(log.getPath());
                }
                if (Objects.nonNull(log.getRequest())) {
                    strBuilder.append("\n# MICRO REQUEST(").append(i).append(")     : ")
                        .append(log.getRequest());
                }
                if (Objects.nonNull(log.getResponse())) {
                    strBuilder.append("\n# MICRO RESPONSE(").append(i).append(")    : ")
                        .append(log.getResponse());
                }
                if (StringUtils.isNotEmpty(log.getDuration())) {
                    strBuilder.append("\n# DURATION(").append(i).append(")          : ")
                        .append(log.getDuration());
                }

                if (i == size) {
                    strBuilder.append("\n--------------------------------------------");
                }
            });
        }

        String stackTractMessage =
            Objects.nonNull(exception) ? ExceptionUtils.getStackTrace(exception) : null;
        if (Objects.nonNull(stackTractMessage)) {
            strBuilder.append("\n").append(stackTractMessage);
        }

        strBuilder.append("\n====================================================================");

        return strBuilder.toString();
    }
}
