package com.example.demo.application.service;

import com.example.demo.application.dto.UserDTO;
import com.example.demo.domain.entity.User;
import com.example.demo.domain.exception.UserAlreadyExistsException;
import com.example.demo.domain.exception.UserNotFoundException;
import com.example.demo.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserDTO.Response createUser(UserDTO.Request request) {
        log.info("Creating user with username: {}", request.getUsername());

        // 检查用户名是否已存在
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UserAlreadyExistsException(request.getUsername());
        }

        // 检查邮箱是否已存在
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException(request.getUsername(), request.getEmail());
        }

        // 创建用户实体，密码使用 BCrypt 加密（日志中不记录密码）
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .phone(request.getPhone())
                .status(User.UserStatus.ACTIVE)
                .build();

        User savedUser = userRepository.save(user);
        log.info("User created successfully with ID: {}", savedUser.getId());

        return UserDTO.Response.fromEntity(savedUser);
    }

    public UserDTO.Response getUserById(Long id) {
        log.info("Fetching user by ID: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        return UserDTO.Response.fromEntity(user);
    }

    public UserDTO.Response getUserByUsername(String username) {
        log.info("Fetching user by username: {}", username);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));
        return UserDTO.Response.fromEntity(user);
    }

    public List<UserDTO.Response> getAllUsers() {
        log.info("Fetching all users");
        return userRepository.findAll().stream()
                .map(UserDTO.Response::fromEntity)
                .collect(Collectors.toList());
    }

    public Page<UserDTO.Response> getUsersByStatus(User.UserStatus status, Pageable pageable) {
        log.info("Fetching users by status: {}", status);
        return userRepository.findByStatus(status, pageable)
                .map(UserDTO.Response::fromEntity);
    }

    public Page<UserDTO.Response> searchUsers(String keyword, Pageable pageable) {
        log.info("Searching users with keyword: {}", keyword);
        return userRepository.searchUsers(keyword, pageable)
                .map(UserDTO.Response::fromEntity);
    }

    @Transactional
    public UserDTO.Response updateUser(Long id, UserDTO.UpdateRequest request) {
        log.info("Updating user with ID: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        // 更新字段
        if (request.getFullName() != null) {
            user.setFullName(request.getFullName());
        }
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }
        if (request.getStatus() != null) {
            user.setStatus(request.getStatus());
        }

        User updatedUser = userRepository.save(user);
        log.info("User updated successfully with ID: {}", updatedUser.getId());

        return UserDTO.Response.fromEntity(updatedUser);
    }

    @Transactional
    public void deleteUser(Long id) {
        log.info("Deleting user with ID: {}", id);
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException(id);
        }
        userRepository.deleteById(id);
        log.info("User deleted successfully with ID: {}", id);
    }

    @Transactional
    public void updateLastLoginTime(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);
    }
}
