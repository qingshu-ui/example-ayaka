package io.github.qingshu.ayaka.example.controller

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

/**
 * Copyright (c) 2024 qingshu.
 * This file is part of the example-ayaka project.
 *
 * This project is licensed under the GPL-3.0 License.
 * See the LICENSE file for details.
 */
@RestController
@RequestMapping("/api/onnx")
class OnnxModel {

    @PostMapping("/detect_image")
    fun detectImage(@RequestParam("imageName") imageName: String): String {

        return ""
    }
}