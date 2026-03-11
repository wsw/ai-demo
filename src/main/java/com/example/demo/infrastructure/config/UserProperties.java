package com.example.demo.infrastructure.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 用户相关配置属性
 */
@Data
@Component
@ConfigurationProperties(prefix = "app.user")
public class UserProperties {

    /**
     * 用户名最小长度
     */
    private int usernameMinLength = 3;

    /**
     * 用户名最大长度
     */
    private int usernameMaxLength = 50;

    /**
     * 用户名正则表达式
     */
    private String usernamePattern = "^[a-zA-Z0-9_]+$";

    /**
     * 密码最小长度
     */
    private int passwordMinLength = 6;

    /**
     * 密码最大长度
     */
    private int passwordMaxLength = 255;

    /**
     * 邮箱最大长度
     */
    private int emailMaxLength = 100;

    /**
     * 全名最大长度
     */
    private int fullNameMaxLength = 100;

    /**
     * 手机号正则表达式
     */
    private String phonePattern = "^$|^[0-9\\-\\+\\s()]+$";

    /**
     * 分页默认大小
     */
    private int defaultPageSize = 10;

    /**
     * 分页最大大小
     */
    private int maxPageSize = 100;
}
