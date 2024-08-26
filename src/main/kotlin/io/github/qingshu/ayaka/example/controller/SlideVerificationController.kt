package io.github.qingshu.ayaka.example.controller

import io.github.qingshu.ayaka.example.yolo.YOLO
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.multipart.MultipartFile
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import javax.imageio.ImageIO

/**
 * Copyright (c) 2024 qingshu.
 * This file is part of the example-ayaka project.
 *
 * This project is licensed under the GPL-3.0 License.
 * See the LICENSE file for details.
 */
@Controller
@RequestMapping("/api/yolo")
class SlideVerificationController {

    @Autowired
    @Qualifier("slideVerification")
    private lateinit var yoloModel: YOLO

    @PostMapping("/recognize")
    fun recognizeImage(@RequestParam("image") file: MultipartFile): ResponseEntity<Map<String, Any>> {
        if (file.contentType?.startsWith("image/") != true) {
            return ResponseEntity.badRequest().body(
                mapOf("error" to "Invalid image format")
            )
        }
        val mat = convertMultipartFileToMat(file)

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

    private fun convertMultipartFileToMat(file: MultipartFile): Mat {
        // Read the image file as bytes
        val imageBytes = file.bytes
        // Convert bytes to InputStream
        val inputStream = ByteArrayInputStream(imageBytes)
        // Read the image as BufferedImage
        val bufferedImage = ImageIO.read(inputStream)
        // Convert BufferedImage to Mat
        val mat = bufferedImageToMat(bufferedImage)
        return mat
    }

    private fun bufferedImageToMat(image: BufferedImage): Mat {
        // 创建一个Mat对象，使用CV_32FC3以保持RGB通道的32位浮点型
        val mat = Mat(image.height, image.width, CvType.CV_32FC3)

        // 获取图像的像素值
        val pixels = IntArray(image.width * image.height)
        image.getRGB(0, 0, image.width, image.height, pixels, 0, image.width)

        // 将像素数据填充到Mat对象中，像素值范围是0到255
        val floatPixels = FloatArray(image.width * image.height * 3)
        for (i in pixels.indices) {
            val pixel = pixels[i]
            floatPixels[3 * i] = (pixel and 0xFF).toFloat()   // Blue
            floatPixels[3 * i + 1] = ((pixel shr 8) and 0xFF).toFloat() // Green
            floatPixels[3 * i + 2] = ((pixel shr 16) and 0xFF).toFloat() // Red
        }

        // 设置Mat的数据
        mat.put(0, 0, floatPixels)
        return mat
    }


}