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
 * Copyright (c) 2024 https://github.com/MisakaTAT/Yuri-Kotlin.
 * This file is part of the https://github.com/MisakaTAT/Yuri-Kotlin project.
 *
 * This file is licensed under the AGPL-3.0 License.
 * See the LICENSE file for details.
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