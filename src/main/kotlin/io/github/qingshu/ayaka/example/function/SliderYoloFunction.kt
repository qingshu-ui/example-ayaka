package io.github.qingshu.ayaka.example.function

import com.fasterxml.jackson.annotation.JsonClassDescription
import io.github.qingshu.ayaka.example.utils.ImgUtils
import io.github.qingshu.ayaka.example.yolo.YOLO
import org.opencv.core.Mat
import org.opencv.core.MatOfByte
import org.opencv.imgcodecs.Imgcodecs
import java.io.File
import java.net.URI
import java.util.*
import java.util.function.Function
import javax.imageio.ImageIO

/**
 * Copyright (c) 2024 qingshu.
 * This file is part of the example-ayaka project.
 *
 * This project is licensed under the GPL-3.0 License.
 * See the LICENSE file for details.
 */
open class SliderYoloFunction(
    private val sliderModel: YOLO
) : Function<SliderYoloFunction.Request, SliderYoloFunction.Response> {

    @JsonClassDescription("For example, igUrlList = {[https://example.com/image1.png]}")
    data class Request(val imgUrlList: List<String> = ArrayList())

    @JsonClassDescription(
        "drewImgMap: {'https://example.com/image1.png': 'https://example.com/image1_drawn.png'}" +
        "The key is the original image URL and the value is the processed image path, which can be " +
        "displayed by calling the function."
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
        val image = ImageIO.read(url)
        val mat = ImgUtils.bufferedImage2Mat(image)
        val detectRel = model.detectObject(mat)
        ImgUtils.drawPredictions(mat, detectRel)
        val imgPath = saveImg(mat)
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