package com.example.demo.domain.exception;

import com.example.demo.infrastructure.exception.BusinessException;

/**
 * 用户已存在异常
 */
public class UserAlreadyExistsException extends BusinessException {
    
    public UserAlreadyExistsException(String username) {
        super("USER_ALREADY_EXISTS", "用户名已存在：" + username, 409);
    }
    
    public UserAlreadyExistsException(String username, String email) {
        super("USER_ALREADY_EXISTS", "用户名或邮箱已存在：" + username + ", " + email, 409);
    }
}
