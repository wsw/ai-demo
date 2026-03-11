package com.example.demo.domain.exception;

import com.example.demo.infrastructure.exception.BusinessException;

/**
 * 无效用户操作异常
 * 用于用户状态不允许的操作
 */
public class InvalidUserOperationException extends BusinessException {
    
    public InvalidUserOperationException(String operation) {
        super("INVALID_USER_OPERATION", "用户状态不允许执行此操作：" + operation, 403);
    }
    
    public InvalidUserOperationException(String operation, String status) {
        super("INVALID_USER_OPERATION", "用户状态为 " + status + " 时不允许执行：" + operation, 403);
    }
}
