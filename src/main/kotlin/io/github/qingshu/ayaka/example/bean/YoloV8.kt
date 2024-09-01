package io.github.qingshu.ayaka.example.bean

import io.github.qingshu.ayaka.example.config.ModelProperties
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

    private val modelCache = mutableMapOf<String, YOLO>()

    private fun getYoloModel(modelName: String): YOLO {
        return modelCache.getOrPut(modelName) {
            val modelPath = reader.get("$modelName-model")
            val labelPath = reader.get("slider-label")
            YOLO.newInstance(modelPath, labelPath)
        }
    }

    @Bean
    @ConditionalOnProperty(
        prefix = "ayaka.slider", name = ["enable"], havingValue = "true", matchIfMissing = true
    )
    fun sliderYoloV8n(): YOLO = getYoloModel("slider-v8n")

    @Bean
    @ConditionalOnProperty(
        prefix = "ayaka.slider", name = ["enable"], havingValue = "true", matchIfMissing = true
    )
    fun sliderYoloV8s(): YOLO = getYoloModel("slider-v8s")

    @Bean
    @ConditionalOnProperty(
        prefix = "ayaka.slider", name = ["enable"], havingValue = "true", matchIfMissing = true
    )
    fun sliderYoloV8m(): YOLO = getYoloModel("slider-v8m")

    @Bean
    @ConditionalOnProperty(
        prefix = "ayaka.slider", name = ["enable"], havingValue = "true", matchIfMissing = true
    )
    fun sliderYoloV8l(): YOLO = getYoloModel("slider-v8l")

    @Bean
    @ConditionalOnProperty(
        prefix = "ayaka.slider", name = ["enable"], havingValue = "true", matchIfMissing = true
    )
    fun sliderYoloV8x(): YOLO = getYoloModel("slider-v8x")

    companion object {
        private val log = LoggerFactory.getLogger(YoloV8::class.java)
    }
}