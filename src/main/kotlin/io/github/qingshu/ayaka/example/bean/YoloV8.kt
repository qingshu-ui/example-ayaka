package io.github.qingshu.ayaka.example.bean

import io.github.qingshu.ayaka.example.config.ModelProperties
import io.github.qingshu.ayaka.example.yolo.YOLO
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy

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
    @Lazy
    fun sliderYoloV8n(): YOLO = getYoloModel("slider-v8n")

    @Bean
    @Lazy
    fun sliderYoloV8s(): YOLO = getYoloModel("slider-v8s")

    @Bean
    @Lazy
    fun sliderYoloV8m(): YOLO = getYoloModel("slider-v8m")

    @Bean
    @Lazy
    fun sliderYoloV8l(): YOLO = getYoloModel("slider-v8l")

    @Bean
    @Lazy
    fun sliderYoloV8x(): YOLO = getYoloModel("slider-v8x")

    companion object {
        private val log = LoggerFactory.getLogger(YoloV8::class.java)
    }
}