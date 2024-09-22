package io.github.qingshu.ayaka.example.config

import cn.hutool.core.io.watch.SimpleWatcher
import cn.hutool.core.io.watch.WatchMonitor
import cn.hutool.core.io.watch.watchers.DelayWatcher
import com.alibaba.fastjson2.JSON
import io.github.qingshu.ayaka.example.annotation.Slf4j
import io.github.qingshu.ayaka.example.annotation.Slf4j.Companion.log
import org.springframework.beans.factory.InitializingBean
import org.springframework.boot.ExitCodeGenerator
import org.springframework.boot.SpringApplication
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.stereotype.Component
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.WatchEvent
import kotlin.system.exitProcess

/**
 * This file is part of the Yuri-Kotlin project:
 * https://github.com/MisakaTAT/Yuri-Kotlin
 *
 * Original Copyright (c) 2024 MisakaTAT
 * Licensed under the APGL-3.0 License. You may obtain a copy of the License at:
 *
 *     https://github.com/MisakaTAT/Yuri-Kotlin/blob/main/LICENSE
 *
 * Modifications:
 * - Modified by qingshu on 2024
 * - ignore
 *
 * This file is licensed under the same APGL-3.0 License.
 */
@Slf4j
@Component
class EAConfig(
    val ctx: ConfigurableApplicationContext,
) : InitializingBean {

    private var isReload = false

    private val configFileName = "config.json"

    private val defaultConfigFileName = "default.config.json"

    companion object {
        lateinit var base: ConfigModel.Base
        lateinit var plugins: ConfigModel.Plugins
    }

    private fun init() {
        try {
            val defaultConfigFile = javaClass.classLoader.getResourceAsStream(defaultConfigFileName)
            val configFile = File(configFileName).apply {
                if (!exists()) {
                    log.error("Config file $configFileName not found, creating default config from $defaultConfigFileName")
                    outputStream().use {
                        defaultConfigFile?.copyTo(it)
                    }
                    log.info("Config file $configFileName created, Please edit it and run it")
                    val exitCode = SpringApplication.exit(ctx, ExitCodeGenerator { 0 })
                    exitProcess(exitCode)
                }
            }
        } catch (e: Exception) {
            log.error("The config file create failed", e)
        }
        Files.newBufferedReader(Paths.get(configFileName)).use { reader ->
            val config: ConfigModel = JSON.parseObject(reader, ConfigModel::class.java)
            base = config.base
            plugins = config.plugins
            if (!isReload) log.info("The config file has been initialized")
        }
    }

    private fun monitor() {
        val monitor = WatchMonitor.createAll("./", object : DelayWatcher(object : SimpleWatcher() {
            override fun onModify(event: WatchEvent<*>?, currentPath: Path?) {
                if (configFileName == event?.context().toString()) {
                    isReload = true
                    init()
                    log.info("The config file has been reloaded")
                }
            }
        }, 500) {})
        monitor.start()
    }

    override fun afterPropertiesSet() {
        init()
        monitor()
    }
}