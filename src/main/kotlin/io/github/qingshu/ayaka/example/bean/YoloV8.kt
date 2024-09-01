package io.github.qingshu.ayaka.example.bean

import io.github.qingshu.ayaka.example.config.PropertiesReader
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
    private lateinit var reader: PropertiesReader

    @Bean
    @Lazy
    fun sliderYoloV8n(): YOLO {
        return YOLO.newInstance(reader.get("slider-v8n-model"), reader.get("slider-label"))
    }

    @Bean
    @Lazy
    fun sliderYoloV8s(): YOLO {
        return YOLO.newInstance(reader.get("slider-v8s-model"), reader.get("slider-label"))
    }

    @Bean
    @Lazy
    fun sliderYoloV8m(): YOLO {
        return YOLO.newInstance(reader.get("slider-v8m-model"), reader.get("slider-label"))
    }

    @Bean
    @Lazy
    fun sliderYoloV8l(): YOLO {
        return YOLO.newInstance(reader.get("slider-v8l-model"), reader.get("slider-label"))
    }

    @Bean
    @Lazy
    fun sliderYoloV8x(): YOLO {
        return YOLO.newInstance(reader.get("slider-v8x-model"), reader.get("slider-label"))
    }

    companion object {
        private val log = LoggerFactory.getLogger(YoloV8::class.java)
    }
}