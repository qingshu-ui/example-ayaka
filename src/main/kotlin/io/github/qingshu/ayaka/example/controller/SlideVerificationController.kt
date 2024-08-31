package io.github.qingshu.ayaka.example.controller

import io.github.qingshu.ayaka.example.service.ImageProcessingService
import io.github.qingshu.ayaka.example.yolo.YOLO
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

/**
 * Copyright (c) 2024 qingshu.
 * This file is part of the example-ayaka project.
 *
 * This project is licensed under the GPL-3.0 License.
 * See the LICENSE file for details.
 */
@RestController
@RequestMapping("/api/yolo")
class SlideVerificationController(
    @Qualifier("sliderYoloV8n") private val yoloModel: YOLO, private val imageProcess: ImageProcessingService
) {

    @PostMapping("/recognize")
    fun recognizeImage(@RequestParam("image") file: MultipartFile): ResponseEntity<Map<String, Any>> {
        if (file.contentType?.startsWith("image/") != true) {
            return ResponseEntity.badRequest().body(
                mapOf("error" to "Invalid image format")
            )
        }
        val mat = imageProcess.convertMultipartFileToMat(file)
        if (mat.empty()) {
            return ResponseEntity.badRequest().body(
                mapOf("error" to "Could not read image")
            )
        }
        val detections = yoloModel.detectObject(mat)
        if (detections.isEmpty()) {
            return ResponseEntity.badRequest().body(
                mapOf("error" to "Could not detect object")
            )
        }
        val result = mapOf("detections" to detections.map {
            mapOf(
                "label" to it.label,
                "labelIndex" to it.labelIndex,
                "bbox" to it.bbox,
                "confidence" to it.confidence,
            )
        })
        return ResponseEntity.ok(result)
    }

}