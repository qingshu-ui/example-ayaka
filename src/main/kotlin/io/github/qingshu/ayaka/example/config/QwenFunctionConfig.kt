package io.github.qingshu.ayaka.example.config

import io.github.qingshu.ayaka.example.function.SliderYoloFunction
import io.github.qingshu.ayaka.example.yolo.YOLO
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Description
import java.util.function.Function

/**
 * Copyright (c) 2024 qingshu.
 * This file is part of the example-ayaka project.
 *
 * This project is licensed under the GPL-3.0 License.
 * See the LICENSE file for details.
 */
@Configuration
class QwenFunctionConfig {

    @Bean
    @Description(
        "If you receive a message containing the image address, you can use " +
        "this function and use the 'showImageWithPath' function to display the processed image."+
        "But do not use the image address in the history message." +
        "The response result must be displayed via the showImageWithPath function.")
    fun detectObjectInImage(model: YOLO): Function<SliderYoloFunction.Request, SliderYoloFunction.Response> {
        return SliderYoloFunction(model)
    }
}