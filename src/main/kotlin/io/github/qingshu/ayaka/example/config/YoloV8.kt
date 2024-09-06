package io.github.qingshu.ayaka.example.config

import io.github.qingshu.ayaka.example.yolo.YOLO
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * Copyright (c) 2024 qingshu.
 * This file is part of the example-ayaka project.
 *
 * This project is licensed under the GPL-3.0 License.
 * See the LICENSE file for details.
 */
@Configuration
class YoloV8 {

    @Autowired
    private lateinit var reader: ModelProperties

    @Bean
    @ConditionalOnProperty(
        prefix = "ayaka.slider", name = ["enable"], havingValue = "true", matchIfMissing = true
    )
    fun sliderYoloModel(): YOLO {
        val modelPath = reader.get("slider-model")
        val labelPath = reader.get("slider-label")
        return YOLO.newInstance(modelPath, labelPath)
    }

    companion object {
        private val log = LoggerFactory.getLogger(YoloV8::class.java)
    }
}