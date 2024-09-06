package io.github.qingshu.ayaka.example.function

import com.fasterxml.jackson.annotation.JsonClassDescription
import io.github.qingshu.ayaka.example.utils.ImgUtils
import io.github.qingshu.ayaka.example.yolo.Detection
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

    @JsonClassDescription(
        "Request object containing a message that may include an image URL. " +
                "The 'imageUrlList' field is a list of strings where each string represents an image URL. " +
                "Each URL should be a valid URL pointing to an image " +
                "Ensure that all URLs in the list are properly formatted and accessible. " +
                "For example, if you have a message like " +
                "'[CQ:image,file=https://multimedia.nt.qq.com.cn/download?appid=1407&amp;fileid=CgoxNzE4NjkyNzQ4EhSQIImFHQKbbQVK1hJd7J-Qm_xsERie3wQg_woovobNga6tiANQgL2jAQ&amp;rkey=CAQSKAB6JWENi5LMakoJ5NH0q3IiR9jYMefShAsvzL0HsjPCMLYmYfC1SmE]', " +
                "you can extract the URL after 'file=' and replace any ';' with '&'. " +
                "For example, the URL 'https://multimedia.nt.qq.com.cn/download?appid=1407&amp;fileid=CgoxNzE4NjkyNzQ4EhSQIImFHQKbbQVK1hJd7J-Qm_xsERie3wQg_woovobNga6tiANQgL2jAQ&amp;rkey=CAQSKAB6JWENi5LMakoJ5NH0q3IiR9jYMefShAsvzL0HsjPCMLYmYfC1SmE' " +
                "should be transformed to 'https://multimedia.nt.qq.com.cn/download?appid=1407&fileid=CgoxNzE4NjkyNzQ4EhSQIImFHQKbbQVK1hJd7J-Qm_xsERie3wQg_woovobNga6tiANQgL2jAQ&rkey=CAQSKAB6JWENi5LMakoJ5NH0q3IiR9jYMefShAsvzL0HsjPCMLYmYfC1SmE'."
    )
    data class Request(val imgUrlList: List<String> = ArrayList())

    @JsonClassDescription(
        "Response object containing the result of processing image URLs. " +
                "The 'imgMap' field is a map where each key is an image URL and each value is a list of detections for that image. " +
                "The key in the map represents the URL of the image that was processed. " +
                "The value is a list of 'Detection' objects that represent the results of detecting objects in the image. " +
                "If no objects are detected in an image, the list will be empty. " +
                "Ensure that each entry in the map corresponds to the URL in the result and contains valid detection results. " +
                "For example, if the request contained the URL 'https://example.com/image1.jpg', " +
                "the response might include an entry with this URL as key and a list of detections as the value, such as: " +
                "{ 'https://example.com/image1.jpg': [Detection(...), Detection(...)] }." +
                "If no objects are detected, the value will be an empty list: { 'https://example.com/image1.jpg': [] }. " +

                "The 'drewImgMap' field is a map where each key is an image URL and each value is a string representation of the image with bounding boxes drawn. " +
                "The value is a string in the format '[CQ:image,file=filepath]' or '[CQ:image,file=filepath&id=2]'. " +
                "This string should be included in the response message exactly as it appears, without any modification. " +
                "Include this string directly in the response to display the annotated image. Do not add additional text or explanations. " +
                "For example, Display the image to the user in the following specified format: '[CQ:image,file=filepath]', " +
                "with markdown format disabled"
    )
    data class Response(
        val imgMap: Map<String, List<Detection>> = mapOf(),
        val drewImgMap: Map<String, String> = mapOf(),
    )

    @Suppress("UNCHECKED_CAST")
    override fun apply(t: Request): Response {
        val imgMap = mutableMapOf<String, List<Detection>>()
        val drewImgMap = mutableMapOf<String, String>()
        t.imgUrlList.forEach { url ->
            val (detections, filePath) = detect(url, sliderModel)
            imgMap[url] = detections as List<Detection>
            drewImgMap[url] = filePath as String
        }
        return Response(imgMap, drewImgMap)
    }

    private fun detect(imgUrl: String, model: YOLO): Array<Any> {
        val url = URI(imgUrl).toURL()
        val image = ImageIO.read(url)
        val mat = ImgUtils.bufferedImage2Mat(image)
        val detectRel = model.detectObject(mat)
        ImgUtils.drawPredictions(mat, detectRel)
        val imgPath = saveImg(mat)
        return arrayOf(detectRel, imgPath)
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