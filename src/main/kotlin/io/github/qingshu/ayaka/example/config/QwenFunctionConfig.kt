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
        "This function processes a list of image URLs and performs object detection using the YOLO model. " +
        "The 'Request' object should contain a list of image URLs. Each URL must be a valid URL pointing to an accessible image. " +
        "The function extracts these URLs, processes each image using the YOLO model to detect objects, " +
        "and returns a 'Response' object. This response includes two maps: " +
        "1. 'imgMap': A map where each key is an image URL and each value is a list of detections for that image. " +
        "   If no objects are detected in an image, the list will be empty. " +
        "   For example, if the request includes the URL 'https://example.com/image1.jpg', " +
        "   the response may contain an entry like: { 'https://example.com/image1.jpg': [Detection(...), Detection(...)] }. " +
        "   If no objects are detected, it will be: { 'https://example.com/image1.jpg': [] }. " +
        "2. 'drewImgMap': A map where each key is an image URL and each value is a string representation of the image with bounding boxes drawn. " +
        "   This string should be in the format '[CQ:image,file=filepath]' or '[CQ:image,file=filepath&id=2]'. " +
        "   This string should be included in the response exactly as it appears, without any modification, to display the annotated image. " +
        "   For example, the response might look like: '[CQ:image,file=filepath]', with markdown format disabled." +
        "   For example, 'Hey, detection object: slider,\n Gap1\n\n [CQ:image,file=filepath]', no markdown format, no markdown format" +
        "Note: Please ensure that the image  URLs provided are not from historical message contents",
    )
    fun detectObjectInImage(model: YOLO): Function<SliderYoloFunction.Request, SliderYoloFunction.Response> {
        return SliderYoloFunction(model)
    }
}