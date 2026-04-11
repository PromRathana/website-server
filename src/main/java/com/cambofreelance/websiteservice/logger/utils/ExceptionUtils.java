package com.cambofreelance.websiteservice.logger.utils;

import com.cambofreelance.websiteservice.logger.contants.ErrorCode;
import com.cambofreelance.websiteservice.logger.contants.LoggerConstant;
import com.cambofreelance.websiteservice.logger.exceptions.AppException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.BindingResult;

import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for handling common exceptions and error message formatting.
 */
public class ExceptionUtils {

    private ExceptionUtils() {
        // Prevent instantiation
    }

    /**
     * Provides a standard multilingual message when the error message is not yet available.
     *
     * @return Map of messages in English, Khmer, and Chinese.
     */
    public static Map<String, String> messageNotFound() {
        Map<String, String> messages = new HashMap<>();
        messages.put(LoggerConstant.MESSAGE, "Message not yet update in our system");
        messages.put(LoggerConstant.MESSAGE_KH,
            "សារមិនទាន់ត្រូវបានធ្វើបច្ចុប្បន្នភាពក្នុងប្រព័ន្ធរបស់យើងទេ។");
        messages.put(LoggerConstant.MESSAGE_CN, "消息尚未在我们的系统中更新。");
        return messages;
    }

    /**
     * Provides a standard multilingual message for service unavailability.
     *
     * @return Map of messages in English, Khmer, and Chinese.
     */
    public static Map<String, String> serviceUnavailable() {
        Map<String, String> messages = new HashMap<>();
        messages.put(LoggerConstant.MESSAGE, "Service is currently unavailable.");
        messages.put(LoggerConstant.MESSAGE_KH, "សេវាកម្មនៅក្នុងពេលបច្ចុប្បន្នមិនអាចប្រើបានទេ។");
        messages.put(LoggerConstant.MESSAGE_CN, "服务当前不可用。");
        return messages;
    }

    /**
     * Provides a standard multilingual message when the database is unavailable.
     *
     * @return Map of messages in English, Khmer, and Chinese.
     */
    public static Map<String, String> databaseUnavailable() {
        Map<String, String> messages = new HashMap<>();
        messages.put(LoggerConstant.MESSAGE,
            "Database is currently unavailable. Please try again later.");
        messages.put(LoggerConstant.MESSAGE_KH,
            "មូលដ្ឋានទិន្នន័យនៅក្នុងពេលបច្ចុប្បន្នមិនអាចប្រើបានទេ។ សូមព្យាយាមម្តងទៀតក្នុងពេលបន្ទាប់។");
        messages.put(LoggerConstant.MESSAGE_CN, "数据库当前不可用，请稍后再试。");
        return messages;
    }

    /**
     * Provides a generic error message in multiple languages for unexpected system errors.
     *
     * @return Map of messages in English, Khmer, and Chinese.
     */
    public static Map<String, String> getGeneralErrorMessage() {
        Map<String, String> messages = new HashMap<>();
        messages.put(LoggerConstant.MESSAGE, "Unexpected error occur. Please try again later.");
        messages.put(LoggerConstant.MESSAGE_KH,
            "កើតមានកំហុសដែលមិនបានរំពឹងទុក។ សូមព្យាយាមម្តងទៀតក្នុងពេលបន្ទាប់។");
        messages.put(LoggerConstant.MESSAGE_CN, "发生了意外错误，请稍后再试。");
        return messages;
    }

    /**
     * Validates a Spring {@link BindingResult} and throws an {@link AppException} if there are any
     * validation errors.
     *
     * @param bindingResult the binding result to validate
     * @throws AppException if validation errors exist
     */
    public static void validateRequest(BindingResult bindingResult) throws AppException {
        if (bindingResult.hasErrors()) {
            throw new AppException(ErrorCode.INVALID_REQ_ERROR, "");
        }
    }

    /**
     * Parses a JSON string to extract and throw an application error if the response indicates a
     * failure.
     *
     * @param jsonStr JSON response string containing error details
     * @throws AppException if the response contains an error code
     */
    public static void validateAndThrowError(String jsonStr) throws AppException {
        if (StringUtils.isEmpty(jsonStr)) {
            return;
        }

        String code = JsonObjectUtils.extractErrorCode(jsonStr);
        boolean success = JsonObjectUtils.extractSuccessAttribute(jsonStr);
        if (!success) {
            String message = JsonObjectUtils.extractJsonFieldByName(jsonStr, "message");
            throw new AppException(code, message);
        }
    }

    public static Throwable getRootCause(Throwable throwable) {

        if (throwable == null) {
            return null;
        }

        Throwable root = throwable;
        while (root.getCause() != null && root.getCause() != root) {
            root = root.getCause();
        }

        return root;
    }
}
