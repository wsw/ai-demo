package com.example.demo.interfaces.rest;

import com.example.demo.application.dto.UserDTO;
import com.example.demo.application.service.UserService;
import com.example.demo.domain.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
@Tag(name = "用户管理", description = "用户相关的 CRUD 操作")
public class UserController {

    private final UserService userService;

    @Operation(summary = "创建用户", description = "创建一个新用户")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "用户创建成功",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserDTO.Response.class))),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "409", description = "用户名或邮箱已存在")
    })
    @PostMapping
    public ResponseEntity<UserDTO.Response> createUser(
            @Valid @RequestBody UserDTO.Request request) {
        log.info("Received request to create user: {}", request.getUsername());
        UserDTO.Response response = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "根据ID获取用户", description = "通过用户ID获取用户详情")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功获取用户"),
            @ApiResponse(responseCode = "404", description = "用户不存在")
    })
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO.Response> getUserById(
            @Parameter(description = "用户ID") @PathVariable Long id) {
        log.info("Received request to get user by ID: {}", id);
        UserDTO.Response response = userService.getUserById(id);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "根据用户名获取用户", description = "通过用户名获取用户详情")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功获取用户"),
            @ApiResponse(responseCode = "404", description = "用户不存在")
    })
    @GetMapping("/username/{username}")
    public ResponseEntity<UserDTO.Response> getUserByUsername(
            @Parameter(description = "用户名") @PathVariable String username) {
        log.info("Received request to get user by username: {}", username);
        UserDTO.Response response = userService.getUserByUsername(username);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "获取所有用户", description = "获取所有用户的列表")
    @ApiResponse(responseCode = "200", description = "成功获取用户列表")
    @GetMapping
    public ResponseEntity<List<UserDTO.Response>> getAllUsers() {
        log.info("Received request to get all users");
        List<UserDTO.Response> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @Operation(summary = "分页获取用户", description = "根据用户状态分页获取用户列表")
    @ApiResponse(responseCode = "200", description = "成功获取用户分页列表")
    @GetMapping("/by-status/{status}")
    public ResponseEntity<Page<UserDTO.Response>> getUsersByStatus(
            @Parameter(description = "用户状态") @PathVariable User.UserStatus status,
            @PageableDefault(size = 10, sort = "createdAt", direction = org.springframework.data.domain.Sort.Direction.DESC) Pageable pageable) {
        log.info("Received request to get users by status: {}", status);
        Page<UserDTO.Response> users = userService.getUsersByStatus(status, pageable);
        return ResponseEntity.ok(users);
    }

    @Operation(summary = "搜索用户", description = "根据关键词搜索用户")
    @ApiResponse(responseCode = "200", description = "成功搜索到用户")
    @GetMapping("/search")
    public ResponseEntity<Page<UserDTO.Response>> searchUsers(
            @Parameter(description = "搜索关键词") @RequestParam String keyword,
            @PageableDefault(size = 10, sort = "createdAt", direction = org.springframework.data.domain.Sort.Direction.DESC) Pageable pageable) {
        log.info("Received request to search users with keyword: {}", keyword);
        Page<UserDTO.Response> users = userService.searchUsers(keyword, pageable);
        return ResponseEntity.ok(users);
    }

    @Operation(summary = "更新用户", description = "根据用户ID更新用户信息")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "用户更新成功"),
            @ApiResponse(responseCode = "404", description = "用户不存在")
    })
    @PutMapping("/{id}")
    public ResponseEntity<UserDTO.Response> updateUser(
            @Parameter(description = "用户ID") @PathVariable Long id,
            @Valid @RequestBody UserDTO.UpdateRequest request) {
        log.info("Received request to update user with ID: {}", id);
        UserDTO.Response response = userService.updateUser(id, request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "删除用户", description = "根据用户ID删除用户")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "用户删除成功"),
            @ApiResponse(responseCode = "404", description = "用户不存在")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "用户ID") @PathVariable Long id) {
        log.info("Received request to delete user with ID: {}", id);
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
