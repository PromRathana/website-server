package com.cambofreelance.websiteservice.logger.exceptions;

import com.cambofreelance.websiteservice.caches.ResponseCodeRedisCache;
import com.cambofreelance.websiteservice.caches.ResponseManagerCache;
import com.cambofreelance.websiteservice.logger.contants.LoggerConstant;
import com.cambofreelance.websiteservice.logger.contants.LoggerErrorCode;
import com.cambofreelance.websiteservice.logger.contants.enums.AcceptLanguage;
import com.cambofreelance.websiteservice.logger.dto.AppLogger;
import com.cambofreelance.websiteservice.logger.dto.BaseResponse;
import com.cambofreelance.websiteservice.logger.dto.ErrorResponse;
import com.cambofreelance.websiteservice.logger.dto.ResponseCodeDto;
import com.cambofreelance.websiteservice.logger.utils.ExceptionUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.sql.SQLException;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@ControllerAdvice
@RequiredArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE)
public class AppLoggerResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    private final AppLogger appLogger;
    private final ResponseCodeRedisCache responseCodeRedisCache;

    /* -------------------------------------------------- */
    /* Resolve error message                              */
    /* -------------------------------------------------- */

    private Map<String, String> resolveMessage(String errorCode) {

        var responseCode = responseCodeRedisCache.getRespCode(errorCode);

        if (Objects.isNull(responseCode)) {
            responseCode = ResponseManagerCache.getRespCode(errorCode);
        }

        return Optional.ofNullable(responseCode)
            .map(ResponseCodeDto::getErrorMessage)
            .orElse(ExceptionUtils.messageNotFound());
    }

    private String resolveI18Message(Map<String, String> message, WebRequest request) {

        String langKey = AcceptLanguage.fromValue(
            request.getHeader(LoggerConstant.ACCEPT_LANGUAGE_HEADER),
            AcceptLanguage.EN).getKey();

        return message.getOrDefault(langKey, LoggerConstant.NA);
    }

    /* -------------------------------------------------- */
    /* Build Response                                     */
    /* -------------------------------------------------- */

    private ErrorResponse buildErrorResponse(Map<String, String> message) {

        return ErrorResponse.builder()
            .messageEn(message.get(LoggerConstant.MESSAGE))
            .messageKm(message.get(LoggerConstant.MESSAGE_KH))
            .messageCh(message.get(LoggerConstant.MESSAGE_CN))
            .httpStatus(message.get(LoggerConstant.HTTP_STATUS))
            .build();
    }

    private BaseResponse<ErrorResponse> buildBaseResponse(
        String code,
        ErrorResponse errorResponse,
        String i18Message) {

        return BaseResponse.<ErrorResponse>builder()
            .code(code)
            .success(false)
            .timestamp(System.currentTimeMillis())
            .data(errorResponse)
            .message(i18Message)
            .build();
    }

    /* -------------------------------------------------- */
    /* Validation Error                                   */
    /* -------------------------------------------------- */

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
        @NonNull MethodArgumentNotValidException ex,
        @NonNull HttpHeaders headers,
        @NonNull HttpStatusCode status,
        @NonNull WebRequest request) {

        appLogger.setException(ex);

        String errors = ex.getBindingResult().getFieldErrors()
            .stream()
            .map(DefaultMessageSourceResolvable::getDefaultMessage)
            .filter(Objects::nonNull)
            .distinct()
            .collect(Collectors.joining(", "));

        Map<String, String> message = resolveMessage(LoggerErrorCode.INVALID_FIELD);
        String i18Message = resolveI18Message(message, request);

        ErrorResponse errorResponse = ErrorResponse.builder()
            .messageEn(formatErrorMessage(message.get(LoggerConstant.MESSAGE), errors))
            .messageKm(formatErrorMessage(message.get(LoggerConstant.MESSAGE_KH), errors))
            .messageCh(formatErrorMessage(message.get(LoggerConstant.MESSAGE_CN), errors))
            .httpStatus(message.get(LoggerConstant.HTTP_STATUS))
            .build();

        BaseResponse<ErrorResponse> baseResponse =
            buildBaseResponse(LoggerErrorCode.BAD_REQUEST, errorResponse,
                String.format("%s [%s]", i18Message, errors));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(baseResponse);
    }

    /* -------------------------------------------------- */
    /* Missing Request Parameter                          */
    /* -------------------------------------------------- */

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(
        MissingServletRequestParameterException ex,
        HttpHeaders headers,
        HttpStatusCode status,
        WebRequest request) {

        appLogger.setException(ex);

        Map<String, String> message = resolveMessage(LoggerErrorCode.INVALID_FIELD);
        String i18Message = resolveI18Message(message, request);

        ErrorResponse errorResponse = buildErrorResponse(message);

        BaseResponse<ErrorResponse> baseResponse =
            buildBaseResponse(LoggerErrorCode.BAD_REQUEST, errorResponse, i18Message);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(baseResponse);
    }

    /* -------------------------------------------------- */
    /* Invalid JSON Body                                  */
    /* -------------------------------------------------- */

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
        @NonNull HttpMessageNotReadableException ex,
        @NonNull HttpHeaders headers,
        @NonNull HttpStatusCode status,
        @NonNull WebRequest request) {

        appLogger.setException(ex);

        Map<String, String> message = resolveMessage(LoggerErrorCode.INVALID_FIELD);
        String i18Message = resolveI18Message(message, request);

        ErrorResponse errorResponse = buildErrorResponse(message);

        BaseResponse<ErrorResponse> baseResponse =
            buildBaseResponse(LoggerErrorCode.INVALID_FIELD, errorResponse, i18Message);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(baseResponse);
    }

    /* -------------------------------------------------- */
    /* Database Connection                                */
    /* -------------------------------------------------- */

    @ExceptionHandler({
        CannotGetJdbcConnectionException.class,
        SQLException.class
    })
    public ResponseEntity<Object> handleDatabaseConnectionException(Exception ex, WebRequest request) {

        appLogger.setException(ex);

        Map<String, String> message = resolveMessage(LoggerErrorCode.DATABASE_CONNECTION_ERROR);
        String i18Message = resolveI18Message(message, request);

        ErrorResponse errorResponse = buildErrorResponse(message);

        BaseResponse<ErrorResponse> baseResponse =
            buildBaseResponse(LoggerErrorCode.DATABASE_CONNECTION_ERROR, errorResponse, i18Message);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(baseResponse);
    }

    /* -------------------------------------------------- */
    /* SQL Constraint                                     */
    /* -------------------------------------------------- */

    @ExceptionHandler({
        DataIntegrityViolationException.class,
        org.hibernate.exception.ConstraintViolationException.class,
        java.sql.SQLIntegrityConstraintViolationException.class
    })
    public ResponseEntity<Object> handleSqlConstraintException(Exception ex, WebRequest request) {

        appLogger.setException(ex);

        Map<String, String> message = resolveMessage(LoggerErrorCode.DATABASE_CONSTRAINT_ERROR);
        String i18Message = resolveI18Message(message, request);

        ErrorResponse errorResponse = buildErrorResponse(message);

        BaseResponse<ErrorResponse> baseResponse =
            buildBaseResponse(LoggerErrorCode.DATABASE_CONSTRAINT_ERROR, errorResponse, i18Message);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(baseResponse);
    }

    /* -------------------------------------------------- */
    /* Database Access                                    */
    /* -------------------------------------------------- */

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<Object> handleDataAccessException(DataAccessException ex, WebRequest request) {

        appLogger.setException(ex);

        Map<String, String> message = resolveMessage(LoggerErrorCode.DATABASE_ERROR);
        String i18Message = resolveI18Message(message, request);

        ErrorResponse errorResponse = buildErrorResponse(message);

        BaseResponse<ErrorResponse> baseResponse =
            buildBaseResponse(LoggerErrorCode.DATABASE_ERROR, errorResponse, i18Message);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(baseResponse);
    }

    /* -------------------------------------------------- */
    /* External Service                                   */
    /* -------------------------------------------------- */

    @ExceptionHandler(WebClientRequestException.class)
    public ResponseEntity<Object> handleWebClientRequestException(
        WebClientRequestException ex,
        WebRequest request) {

        appLogger.setException(ex);

        Map<String, String> message = resolveMessage(LoggerErrorCode.WEB_CLIENT_REQUEST_ERROR);
        String i18Message = resolveI18Message(message, request);

        ErrorResponse errorResponse = buildErrorResponse(message);

        BaseResponse<ErrorResponse> baseResponse =
            buildBaseResponse(LoggerErrorCode.WEB_CLIENT_REQUEST_ERROR, errorResponse, i18Message);

        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(baseResponse);
    }

    /* -------------------------------------------------- */
    /* Business Exception                                 */
    /* -------------------------------------------------- */

    @ExceptionHandler(AppException.class)
    public ResponseEntity<Object> handleAppException(AppException ex, WebRequest request) {

        appLogger.setException(ex);

        Map<String, String> message = resolveMessage(ex.getErrorCode());
        String i18Message = resolveI18Message(message, request);

        if (Objects.equals(i18Message, "Message not yet update in our system")) {
            i18Message = ex.getMessage();
        }

        ErrorResponse errorResponse = buildErrorResponse(message);

        BaseResponse<ErrorResponse> baseResponse =
            buildBaseResponse(ex.getErrorCode(), errorResponse, i18Message);

        int httpStatus = Strings.isEmpty(errorResponse.getHttpStatus())
            ? 200
            : Integer.parseInt(errorResponse.getHttpStatus());

        return ResponseEntity.status(httpStatus).body(baseResponse);
    }

    /* -------------------------------------------------- */
    /* General Exception                                  */
    /* -------------------------------------------------- */

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGeneralException(Exception ex, WebRequest request) {

        appLogger.setException(ex);

        // Get root cause message
        Throwable rootCause = ExceptionUtils.getRootCause(ex);
        String errorMessage = rootCause != null ? rootCause.getMessage() : ex.getMessage();

        Map<String, String> message = resolveMessage(LoggerErrorCode.INTERNAL_SERVER_ERROR);
        String i18Message = resolveI18Message(message, request);

        // Override message if exception message exists
        if (errorMessage != null && !errorMessage.isBlank()) {
            i18Message = errorMessage;
        }

        ErrorResponse errorResponse = ErrorResponse.builder()
            .messageEn(errorMessage != null ? errorMessage : message.get(LoggerConstant.MESSAGE))
            .messageKm(message.get(LoggerConstant.MESSAGE_KH))
            .messageCh(message.get(LoggerConstant.MESSAGE_CN))
            .httpStatus(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()))
            .build();

        BaseResponse<ErrorResponse> baseResponse =
            buildBaseResponse(LoggerErrorCode.INTERNAL_SERVER_ERROR, errorResponse, i18Message);

        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(baseResponse);
    }
    /* -------------------------------------------------- */

    private String formatErrorMessage(String baseMessage, String errors) {

        if (baseMessage == null) {
            baseMessage = "";
        }

        return errors == null || errors.isBlank()
            ? baseMessage
            : String.format("%s [%s]", baseMessage, errors);
    }
}