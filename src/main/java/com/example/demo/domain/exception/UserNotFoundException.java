package com.example.demo.domain.exception;

import com.example.demo.infrastructure.exception.BusinessException;

/**
 * 用户不存在异常
 */
public class UserNotFoundException extends BusinessException {
    
    public UserNotFoundException(Long id) {
        super("USER_NOT_FOUND", "用户不存在，ID: " + id, 404);
    }
    
    public UserNotFoundException(String username) {
        super("USER_NOT_FOUND", "用户不存在：" + username, 404);
    }
}
