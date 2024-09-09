package io.github.qingshu.ayaka.example.utils

import io.github.qingshu.ayaka.example.yolo.Detection
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.core.Point
import org.opencv.core.Scalar
import org.opencv.imgproc.Imgproc
import java.awt.image.BufferedImage
import java.awt.image.DataBufferByte
import java.net.HttpURLConnection
import java.net.URL

object ImgUtils {

    private val colorMap = mapOf(
        0 to Scalar(220.0, 50.0, 0.0),
        1 to Scalar(0.0, 200.0, 0.0),
        2 to Scalar(0.0, 0.0, 200.0),
        3 to Scalar(200.0, 200.0, 0.0),
        4 to Scalar(200.0, 0.0, 200.0),
        5 to Scalar(0.0, 200.0, 200.0),
        6 to Scalar(200.0, 100.0, 60.0),
        7 to Scalar(60.0, 50.0, 249.0),
        8 to Scalar(10.0, 60.0, 249.0),
        9 to Scalar(60.0, 100.0, 10.0),
    )

    fun bufferedImage2Mat(image: BufferedImage): Mat {
        val mat = Mat(image.height, image.width, CvType.CV_8UC3)
        val supportType = listOf(
            BufferedImage.TYPE_INT_RGB,
            BufferedImage.TYPE_INT_BGR,
            BufferedImage.TYPE_3BYTE_BGR,
            BufferedImage.TYPE_USHORT_565_RGB,
            BufferedImage.TYPE_USHORT_555_RGB,
        )
        if (supportType.contains(image.type)) {
            mat.put(0, 0, (image.data.dataBuffer as DataBufferByte).data)
            return mat
        }
        toSupportType(image, mat)
        return mat
    }

    private fun toSupportType(image: BufferedImage, mat: Mat) {
        val pixels = IntArray(image.width * image.height)
        image.getRGB(0, 0, image.width, image.height, pixels, 0, image.width)
        val bytePixels = ByteArray(image.width * image.height * 3)
        for (i in pixels.indices) {
            val pixel = pixels[i]
            bytePixels[3 * i] = (pixel and 0xFF).toByte()   // Blue
            bytePixels[3 * i + 1] = ((pixel shr 8) and 0xFF).toByte() // Green
            bytePixels[3 * i + 2] = ((pixel shr 16) and 0xFF).toByte() // Red
        }
        mat.put(0, 0, bytePixels)
    }

    fun drawPredictions(img: Mat, detections: List<Detection>) {
        for (detection in detections) {
            val bbox = detection.bbox
            val color = colorMap[detection.labelIndex]
            Imgproc.rectangle(
                img,
                Point(bbox[0].toDouble(), bbox[1].toDouble()),
                Point(bbox[2].toDouble(), bbox[3].toDouble()),
                color,
                1
            )
            Imgproc.putText(
                img,
                detection.label,
                Point(bbox[0] - 1.toDouble(), bbox[1] - 20.toDouble()),
                Imgproc.FONT_HERSHEY_PLAIN,
                1.0,
                color
            )
            Imgproc.putText(
                img,
                String.format("%.2f", detection.confidence),
                Point(bbox[0] - 1.toDouble(), bbox[1] - 5.toDouble()),
                Imgproc.FONT_HERSHEY_PLAIN,
                1.0,
                color
            )
        }
    }
}