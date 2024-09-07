package io.github.qingshu.ayaka.example.config

import io.github.qingshu.ayaka.example.annotation.Slf4j
import io.github.qingshu.ayaka.example.annotation.Slf4j.Companion.log
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.FileSystemResource
import org.springframework.stereotype.Component
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.util.*
import kotlin.system.exitProcess

/**
 * Copyright (c) 2024 qingshu.
 * This file is part of the example-ayaka project.
 *
 * This project is licensed under the GPL-3.0 License.
 * See the LICENSE file for details.
 */
@Slf4j
@Component
@ConfigurationProperties(prefix = "ayaka.slider")
class ModelProperties {
    private var properties = Properties()
    private val configPath = "."
    private val configName = "slider-model.yaml"

    /**
     * 是否启用 滑块验证码识别模型
     */
    var enable = true

    init {
        val configFilePath = "$configPath/$configName"
        val configFile = File(configFilePath)
        if (!configFile.exists()) {
            if (!configFile.parentFile.exists()) {
                configFile.parentFile.mkdirs()
            }
            try {
                val defaultConfig = ClassPathResource("slider-model.yaml")
                Files.copy(
                    defaultConfig.inputStream, configFile.toPath(), StandardCopyOption.REPLACE_EXISTING
                )
                log.warn("Configuration file not found. A default configuration the has been copied to $configFilePath")
                log.info("Please modify the configuration file and run it again")
                exitProcess(0)
            } catch (e: IOException) {
                log.error("Unable to create default config. ${e.message}")
            }
        }
        val yamlFactory = YamlPropertiesFactoryBean()
        yamlFactory.setResources(FileSystemResource(configFilePath))
        properties = yamlFactory.`object` ?: Properties()
    }

    fun get(key: String): String {
        return properties.getProperty(key) ?: ""
    }
}