package io.github.qingshu.ayaka.example.function

import com.fasterxml.jackson.annotation.JsonClassDescription
import io.github.qingshu.ayaka.example.annotation.Slf4j
import io.github.qingshu.ayaka.example.annotation.Slf4j.Companion.log
import io.github.qingshu.ayaka.example.utils.ImgUtils
import io.github.qingshu.ayaka.example.yolo.YOLO
import org.opencv.core.Mat
import org.opencv.core.MatOfByte
import org.opencv.imgcodecs.Imgcodecs
import java.io.File
import java.net.HttpURLConnection
import java.net.URI
import java.util.*
import java.util.function.Function

/**
 * Copyright (c) 2024 qingshu.
 * This file is part of the example-ayaka project.
 *
 * This project is licensed under the GPL-3.0 License.
 * See the LICENSE file for details.
 */
@Slf4j
open class SliderYoloFunction(
    private val sliderModel: YOLO
) : Function<SliderYoloFunction.Request, SliderYoloFunction.Response> {

    @JsonClassDescription("For example, igUrlList = {[https://example.com/image1.png]}")
    data class Request(val imgUrlList: List<String> = ArrayList())

    @JsonClassDescription(
        "drewImgMap: {'https://example.com/image1.png': 'https://example.com/image1_drawn.png'} " +
                "The key is the original image URL and the value is the processed image path, which can be, " +
                "If the value is empty string or is not the format of the file path, " +
                "it means that the image cannot be processed and is ignored. "
    )
    data class Response(
        val drewImgMap: Map<String, String> = mapOf(),
    )

    override fun apply(t: Request): Response {
        val drewImgMap = mutableMapOf<String, String>()
        t.imgUrlList.forEach { url ->
            val filePath = detect(url, sliderModel)
            drewImgMap[url] = filePath
        }
        return Response(drewImgMap)
    }

    private fun detect(imgUrl: String, model: YOLO): String {
        val url = URI(imgUrl).toURL()

        lateinit var mat: Mat

        val conn = url.openConnection() as? HttpURLConnection
        try {
            if (conn?.contentType?.startsWith("image/") != true) {
                log.warn("Not a valid image format: ${conn?.contentType}")
                return ""
            }
            conn.inputStream.use {
                val bytes = it.readBytes()
                mat = Imgcodecs.imdecode(MatOfByte(*bytes), Imgcodecs.IMREAD_COLOR)
                if (mat.empty()) {
                    log.warn("Image loading failed: {}", imgUrl)
                    return ""
                }
            }
        } finally {
            conn?.disconnect()
        }

        val detectRel = model.detectObject(mat)
        ImgUtils.drawPredictions(mat, detectRel)
        val imgPath = saveImg(mat)
        mat.release() // Optional, because mat is managed by OpenCV
        return imgPath
    }

    private fun mat2Base64(mat: Mat): String {
        val matOfByte = MatOfByte()
        Imgcodecs.imencode(".png", mat, matOfByte)
        val base64Str = Base64.getEncoder().encodeToString(matOfByte.toArray())
        return base64Str
    }

    private fun saveImg(mat: Mat): String {
        val outPath = "detection"
        val outName = UUID.randomUUID().toString()
        File(outPath).apply {
            if (!exists()) mkdirs()
        }
        Imgcodecs.imwrite("$outPath/$outName.png", mat)
        val file = File(outPath, "$outName.png")
        return file.absolutePath
    }
}