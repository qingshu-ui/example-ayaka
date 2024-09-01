package io.github.qingshu.ayaka.example.config

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.io.IOException
import java.util.*

/**
 * Copyright (c) 2024 qingshu.
 * This file is part of the example-ayaka project.
 *
 * This project is licensed under the GPL-3.0 License.
 * See the LICENSE file for details.
 */
@Component
class ModelProperties {
    private val properties = Properties()
    private val configPath = "./model"
    private val configName = "slider-model.properties"

    init {
        try {
            properties.load(javaClass.getResourceAsStream("/slider-model.properties"))
        } catch (e: IOException) {
            log.error("Could not load slider-model.properties, because ${e.message}")
        }
    }

    fun get(key: String): String {
        return properties.getProperty(key)
    }

    companion object {
        private val log = LoggerFactory.getLogger(ModelProperties::class.java)
    }
}