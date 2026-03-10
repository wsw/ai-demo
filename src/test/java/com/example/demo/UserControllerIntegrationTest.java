package com.example.demo;

import com.example.demo.application.dto.UserDTO;
import com.example.demo.domain.entity.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("demo")
@DisplayName("用户控制器集成测试")
class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private UserDTO.Request createUserRequest;

    @BeforeEach
    void setUp() {
        createUserRequest = UserDTO.Request.builder()
                .username("testuser" + System.currentTimeMillis())
                .email("test" + System.currentTimeMillis() + "@example.com")
                .password("password123")
                .fullName("Test User")
                .phone("13800138000")
                .build();
    }

    @Test
    @DisplayName("测试创建用户 - 成功")
    void createUser_Success() throws Exception {
        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createUserRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.username", is(createUserRequest.getUsername())))
                .andExpect(jsonPath("$.email", is(createUserRequest.getEmail())))
                .andExpect(jsonPath("$.fullName", is(createUserRequest.getFullName())))
                .andExpect(jsonPath("$.status", is("ACTIVE")));
    }

    @Test
    @DisplayName("测试创建用户 - 用户名已存在")
    void createUser_DuplicateUsername() throws Exception {
        // 首先创建一个用户
        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createUserRequest)))
                .andExpect(status().isCreated());

        // 尝试使用相同的用户名再次创建
        UserDTO.Request duplicateRequest = UserDTO.Request.builder()
                .username(createUserRequest.getUsername())
                .email("different@example.com")
                .password("password123")
                .build();

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(duplicateRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Bad Request")));
    }

    @Test
    @DisplayName("测试创建用户 - 参数验证失败")
    void createUser_ValidationFailed() throws Exception {
        UserDTO.Request invalidRequest = UserDTO.Request.builder()
                .username("ab") // 太短
                .email("invalid-email") // 格式不正确
                .password("123") // 太短
                .build();

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Validation Failed")));
    }

    @Test
    @DisplayName("测试根据ID获取用户 - 成功")
    void getUserById_Success() throws Exception {
        // 首先创建一个用户
        MvcResult createResult = mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createUserRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        UserDTO.Response createdUser = objectMapper.readValue(
                createResult.getResponse().getContentAsString(),
                UserDTO.Response.class);

        // 然后查询该用户
        mockMvc.perform(get("/api/v1/users/{id}", createdUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(createdUser.getId().intValue())))
                .andExpect(jsonPath("$.username", is(createUserRequest.getUsername())));
    }

    @Test
    @DisplayName("测试根据ID获取用户 - 用户不存在")
    void getUserById_NotFound() throws Exception {
        mockMvc.perform(get("/api/v1/users/{id}", 99999))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("Not Found")));
    }

    @Test
    @DisplayName("测试获取所有用户")
    void getAllUsers() throws Exception {
        // 创建几个用户
        for (int i = 0; i < 3; i++) {
            UserDTO.Request request = UserDTO.Request.builder()
                    .username("user" + i + "_" + System.currentTimeMillis())
                    .email("user" + i + "_" + System.currentTimeMillis() + "@example.com")
                    .password("password123")
                    .build();

            mockMvc.perform(post("/api/v1/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated());
        }

        // 获取所有用户
        mockMvc.perform(get("/api/v1/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(3))));
    }

    @Test
    @DisplayName("测试更新用户 - 成功")
    void updateUser_Success() throws Exception {
        // 首先创建一个用户
        MvcResult createResult = mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createUserRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        UserDTO.Response createdUser = objectMapper.readValue(
                createResult.getResponse().getContentAsString(),
                UserDTO.Response.class);

        // 更新用户
        UserDTO.UpdateRequest updateRequest = UserDTO.UpdateRequest.builder()
                .fullName("Updated Name")
                .phone("13900139000")
                .status(User.UserStatus.INACTIVE)
                .build();

        mockMvc.perform(put("/api/v1/users/{id}", createdUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName", is("Updated Name")))
                .andExpect(jsonPath("$.phone", is("13900139000")))
                .andExpect(jsonPath("$.status", is("INACTIVE")));
    }

    @Test
    @DisplayName("测试删除用户 - 成功")
    void deleteUser_Success() throws Exception {
        // 首先创建一个用户
        MvcResult createResult = mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createUserRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        UserDTO.Response createdUser = objectMapper.readValue(
                createResult.getResponse().getContentAsString(),
                UserDTO.Response.class);

        // 删除用户
        mockMvc.perform(delete("/api/v1/users/{id}", createdUser.getId()))
                .andExpect(status().isNoContent());

        // 验证用户已被删除
        mockMvc.perform(get("/api/v1/users/{id}", createdUser.getId()))
                .andExpect(status().isNotFound());
    }
}
