package com.example.demo.infrastructure.exception;

import lombok.Getter;

/**
 * 业务异常基类
 * 所有业务相关异常应继承此类
 */
@Getter
public class BusinessException extends RuntimeException {
    
    private final String errorCode;
    private final int statusCode;
    
    public BusinessException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.statusCode = 400;
    }
    
    public BusinessException(String errorCode, String message, int statusCode) {
        super(message);
        this.errorCode = errorCode;
        this.statusCode = statusCode;
    }
    
    public BusinessException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.statusCode = 400;
    }
}
