package io.github.qingshu.ayaka.example.bean

import io.github.qingshu.ayaka.example.config.PropertiesReader
import io.github.qingshu.ayaka.example.yolo.YOLO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
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
    private lateinit var reader: PropertiesReader

    @Bean("slideVerification")
    @ConditionalOnMissingBean
    fun onnxWithYoloV8(): YOLO {
        val classLoader = YoloV8::class.java.classLoader
        val modelPath = classLoader.getResource(reader.get("slide_verification_model"))?.path?.removePrefix("/")
        val labelPath = classLoader.getResource(reader.get("slide_verification_names"))?.path?.removePrefix("/")
        return YOLO(modelPath!!, labelPath!!)
    }
}