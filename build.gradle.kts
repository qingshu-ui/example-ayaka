plugins {
    kotlin("jvm") version "1.9.24"
    kotlin("plugin.spring") version "1.9.24"
    kotlin("kapt") version "1.9.24"
    kotlin("plugin.jpa") version "1.9.24"
    id("org.springframework.boot") version "3.3.2"
    id("io.spring.dependency-management") version "1.1.6"
}

group = "io.github.qingshu-ui"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenLocal()
    mavenCentral()
    maven { url = uri("https://repo.spring.io/milestone") }
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation ("com.github.qingshu-ui:ayaka-spring-boot-starter:1.0.1")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")

    // spring
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation ("org.springframework.boot:spring-boot-starter-aop")
    implementation("org.springframework.boot:spring-boot-starter-websocket")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-jdbc")
    implementation("org.xerial:sqlite-jdbc:3.45.2.0")
    implementation("org.hibernate.orm:hibernate-community-dialects:6.4.4.Final")
    kapt("org.springframework.boot:spring-boot-configuration-processor")
    implementation("com.github.dromara.hutool:hutool-core:5.8.29")
    implementation("com.github.dromara.hutool:hutool-system:5.8.29")
    implementation("net.jodah:expiringmap:0.5.10")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}
