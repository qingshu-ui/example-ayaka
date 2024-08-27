package io.github.qingshu.ayaka.example.service

import org.opencv.core.Mat
import org.springframework.web.multipart.MultipartFile

/**
 * Copyright (c) 2024 qingshu.
 * This file is part of the example-ayaka project.
 *
 * This project is licensed under the GPL-3.0 License.
 * See the LICENSE file for details.
 */
interface ImageProcessingService {

    /**
     * 将 MultipartFile 转为模型需要的 Mat 对象
     */
    fun convertMultipartFileToMat(file: MultipartFile): Mat
}