plugins {
    kotlin("jvm") version "1.9.24"
    kotlin("plugin.spring") version "1.9.24"
    kotlin("kapt") version "1.9.24"
    id("org.springframework.boot") version "3.3.2"
    id("io.spring.dependency-management") version "1.1.6"
}
val springAiVersion by extra("1.0.0-M2")

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
    maven {
        url = uri("https://maven.pkg.github.com/qingshu-ui/ayaka-spring-boot-starter")
        credentials {
            username = System.getenv("GITHUB_ACTOR")
            password = System.getenv("GITHUB_TOKEN")
        }
    }
    maven { url = uri("https://maven.meteordev.org/releases") }
    maven { url = uri("https://repo.spring.io/milestone") }
}

dependencies {
    // onnx and opencv
    implementation("ai.djl.opencv:opencv:0.29.0")
    implementation("com.microsoft.onnxruntime:onnxruntime:1.19.0")

    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation ("io.github.qingshu-ui:ayaka-spring-boot-starter:0.0.6-SNAPSHOT")

    // spring
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation ("org.springframework.boot:spring-boot-starter-aop")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("org.springframework.boot:spring-boot-starter-websocket")
    implementation("org.springframework.ai:spring-ai-openai-spring-boot-starter")
    kapt("org.springframework.boot:spring-boot-configuration-processor")
}
dependencyManagement {
    imports {
        mavenBom("org.springframework.ai:spring-ai-bom:$springAiVersion")
    }
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}
