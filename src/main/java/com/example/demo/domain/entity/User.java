package com.example.demo.domain.entity;

import com.example.demo.domain.exception.InvalidUserOperationException;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", nullable = false, unique = true, length = 50)
    private String username;

    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Column(name = "full_name", length = 100)
    private String fullName;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private UserStatus status = UserStatus.ACTIVE;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    // ==================== 领域行为方法 ====================
    
    /**
     * 激活用户
     * 将用户状态从 INACTIVE 或 SUSPENDED 恢复为 ACTIVE
     */
    public void activate() {
        if (this.status == UserStatus.DELETED) {
            throw new InvalidUserOperationException("activate", "DELETED");
        }
        this.status = UserStatus.ACTIVE;
    }
    
    /**
     * 停用用户
     * 将用户状态设置为 INACTIVE
     */
    public void deactivate() {
        if (this.status == UserStatus.DELETED) {
            throw new InvalidUserOperationException("deactivate", "DELETED");
        }
        this.status = UserStatus.INACTIVE;
    }
    
    /**
     * 冻结用户
     * 将用户状态设置为 SUSPENDED，通常用于违规处罚
     */
    public void suspend() {
        if (this.status == UserStatus.DELETED) {
            throw new InvalidUserOperationException("suspend", "DELETED");
        }
        this.status = UserStatus.SUSPENDED;
    }
    
    /**
     * 更新最后登录时间
     * 记录用户最后登录的时间
     */
    public void updateLastLoginTime() {
        if (this.status == UserStatus.DELETED) {
            throw new InvalidUserOperationException("login", "DELETED");
        }
        if (this.status == UserStatus.SUSPENDED) {
            throw new InvalidUserOperationException("login", "SUSPENDED");
        }
        if (this.status == UserStatus.INACTIVE) {
            throw new InvalidUserOperationException("login", "INACTIVE");
        }
        this.lastLoginAt = LocalDateTime.now();
    }
    
    /**
     * 软删除用户
     * 将用户状态设置为 DELETED，不实际删除数据
     */
    public void softDelete() {
        this.status = UserStatus.DELETED;
        this.username = null;
        this.email = null;
        this.fullName = null;
        this.phone = null;
    }

    public enum UserStatus {
        ACTIVE, INACTIVE, SUSPENDED, DELETED
    }
}
