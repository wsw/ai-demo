plugins {
	java
	id("org.springframework.boot") version "4.0.3"
	id("io.spring.dependency-management") version "1.1.7"
	id("org.asciidoctor.jvm.convert") version "4.0.5"
	id("com.google.cloud.tools.jib") version "3.4.0"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"
description = "Demo project for Spring Boot"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

extra["snippetsDir"] = file("build/generated-snippets")
extra["springModulithVersion"] = "2.0.3"

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-flyway")
	implementation("org.springframework.boot:spring-boot-starter-kafka")
//	implementation("org.springframework.boot:spring-boot-starter-opentelemetry")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:3.0.2")
	implementation("org.springframework.modulith:spring-modulith-events-api")
	implementation("org.springframework.modulith:spring-modulith-starter-core")
	implementation("org.springframework.modulith:spring-modulith-starter-jpa")
	implementation(platform("me.paulschwarz:spring-dotenv-bom:5.1.0"))
	implementation("me.paulschwarz:springboot4-dotenv")
	compileOnly("org.projectlombok:lombok")
	developmentOnly("org.springframework.boot:spring-boot-devtools")
	// developmentOnly("org.springframework.boot:spring-boot-docker-compose")
	runtimeOnly("org.springframework.modulith:spring-modulith-actuator")
	runtimeOnly("org.springframework.modulith:spring-modulith-events-kafka")
	runtimeOnly("org.springframework.modulith:spring-modulith-observability")
	runtimeOnly("com.mysql:mysql-connector-j")
	
	// H2 Database for demo/testing
	runtimeOnly("com.h2database:h2")
	
	// Flyway MySQL support
	implementation("org.flywaydb:flyway-mysql")
	
	// Validation
	implementation("jakarta.validation:jakarta.validation-api")
	implementation("org.hibernate.validator:hibernate-validator")
	annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
	annotationProcessor("org.projectlombok:lombok")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
}

dependencyManagement {
	imports {
		mavenBom("org.springframework.modulith:spring-modulith-bom:${property("springModulithVersion")}")
	}
}

// Jib Docker 镜像配置
// 镜像源选项（根据网络环境选择）:
//   1. Docker Hub (官方): eclipse-temurin:21-jre-alpine
//   2. 腾讯云 (免费): ccr.ccs.tencentyun.com/library/eclipse-temurin:21-jre-alpine
//   3. 华为云 (免费): swr.cn-east-2.myhuaweicloud.com/library/eclipse-temurin:21-jre-alpine
//   4. 阿里云 (需认证): registry.cn-hangzhou.aliyuncs.com/library/eclipse-temurin:21-jre-alpine
jib {
	from {
		image = "eclipse-temurin:21-jre-alpine"
		platforms {
			platform {
				os = "linux"
				architecture = "amd64"
			}
		}
	}
	to {
		image = "demo:0.0.1-SNAPSHOT"
		tags = setOf("latest", version.toString())
	}
	container {
		ports = listOf("8080", "8081")
		labels = mapOf(
			"maintainer" to "Weishuwen",
			"version" to version.toString()
		)
		jvmFlags = listOf(
			"-Xms256m",
			"-Xmx512m",
			"-XX:+UseG1GC",
			"-Djava.security.egd=file:/dev/./urandom",
			"-Duser.timezone=Asia/Shanghai"
		)
		environment = mapOf(
			"SPRING_PROFILES_ACTIVE" to "prod",
			"TZ" to "Asia/Shanghai"
		)
		creationTime = "USE_CURRENT_TIMESTAMP"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
	// 添加 Mockito agent 支持 Java 21
	jvmArgs = listOf(
		"-XX:+EnableDynamicAgentLoading",
		"-Xshare:off"
	)
}

tasks.test {
	outputs.dir(project.extra["snippetsDir"]!!)
}

tasks.asciidoctor {
	inputs.dir(project.extra["snippetsDir"]!!)
	dependsOn(tasks.test)
}

// 构建脚本的仓库配置
buildscript {
    repositories {
        maven(url = "https://maven.aliyun.com/repository/public/")
        maven(url = "https://maven.aliyun.com/repository/gradle-plugin/")
        mavenCentral()
        maven(url = "https://maven.aliyun.com/repository/google/")
    }
}

