package com.example.demo.interfaces.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.BuildProperties;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/health")
@RequiredArgsConstructor
@Tag(name = "健康检查", description = "系统健康检查和状态监控")
public class HealthController {

    private final Environment environment;
    
    private BuildProperties buildProperties;

    @Autowired(required = false)
    public void setBuildProperties(BuildProperties buildProperties) {
        this.buildProperties = buildProperties;
    }

    @Operation(summary = "健康检查", description = "检查应用是否正常运行")
    @GetMapping
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("timestamp", LocalDateTime.now().toString());
        if (buildProperties != null) {
            response.put("service", buildProperties.getName());
            response.put("version", buildProperties.getVersion());
        } else {
            response.put("service", "demo");
            response.put("version", "0.0.1-SNAPSHOT");
        }
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "应用信息", description = "获取应用的详细信息和构建属性")
    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> info() {
        Map<String, Object> response = new HashMap<>();

        // 构建信息
        Map<String, Object> build = new HashMap<>();
        if (buildProperties != null) {
            build.put("name", buildProperties.getName());
            build.put("version", buildProperties.getVersion());
            build.put("time", buildProperties.getTime());
        } else {
            build.put("name", "demo");
            build.put("version", "0.0.1-SNAPSHOT");
            build.put("time", LocalDateTime.now().toString());
        }
        response.put("build", build);

        // 环境信息
        Map<String, Object> env = new HashMap<>();
        env.put("activeProfiles", Arrays.asList(environment.getActiveProfiles()));
        env.put("defaultProfiles", Arrays.asList(environment.getDefaultProfiles()));
        response.put("environment", env);

        // Java 信息
        Map<String, Object> java = new HashMap<>();
        java.put("version", System.getProperty("java.version"));
        java.put("vendor", System.getProperty("java.vendor"));
        response.put("java", java);

        response.put("timestamp", LocalDateTime.now().toString());

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "就绪检查", description = "检查应用是否已准备好接收流量")
    @GetMapping("/ready")
    public ResponseEntity<Map<String, Object>> ready() {
        Map<String, Object> response = new HashMap<>();
        response.put("ready", true);
        response.put("timestamp", LocalDateTime.now().toString());
        return ResponseEntity.ok(response);
    }
}
