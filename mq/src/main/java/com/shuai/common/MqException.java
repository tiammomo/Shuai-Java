package com.shuai.common;

/**
 * MQ 通用异常
 *
 * @author Shuai
 */
public class MqException extends RuntimeException {

    private final String errorCode;

    public MqException(String message) {
        super(message);
        this.errorCode = "MQ_ERROR";
    }

    public MqException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public MqException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "MQ_ERROR";
    }

    public MqException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
