package io.github.qingshu.ayaka.example.yolo.compatible

import org.opencv.core.Mat
import org.opencv.core.Scalar
import org.opencv.core.Size
import org.opencv.imgproc.Imgproc

/**
 * Copyright (c) 2024 qingshu.
 * This file is part of the example-ayaka project.
 *
 * This project is licensed under the GPL-3.0 License.
 * See the LICENSE file for details.
 */
open class AbstractONNX : ONNX {

    /**
     * 以填充的形式调整图像，以符合目标大小
     * @param mat [Mat]
     * @param targetSize [Size]
     * @return [Mat]
     */
    override fun scaleByPadding(mat: Mat, targetSize: Size): Mat {

        // 计算目标之间的缩放比列
        val aspectRatio = minOf(targetSize.width / mat.width(), targetSize.height / mat.height())
        // 计算缩放后的图像尺寸
        val newSize = Size(mat.width() * aspectRatio, mat.height() * aspectRatio)

        // 创建一个目标图像，并初始化为0（黑色），其尺寸为目标大小
        val dst = Mat.zeros(targetSize.height.toInt(), targetSize.width.toInt(), mat.type())
        dst.setTo(Scalar(114.0, 114.0, 114.0))

        // 创建一个用于缩放后图像的 Mat 对象
        val scaledImage = Mat()
        // 缩放源图像
        Imgproc.resize(mat, scaledImage, newSize)

        // 计算缩放后图像在目标图像中的偏移量，以使其居中对齐
        val xOffset = ((targetSize.width - newSize.width) / 2).toInt()
        val yOffset = ((targetSize.height - newSize.height) / 2).toInt()

        // 将缩放后的图像复制到目标图像的中心区域
        scaledImage.copyTo(
            dst.rowRange(yOffset, yOffset + newSize.height.toInt()).colRange(xOffset, xOffset + newSize.width.toInt())
        )

        // 返回结果
        return dst
    }

    /**
     * 将 宽、高、通道 转换为 通道、宽、高
     * whc to cwh
     * @param arr [FloatArray]
     * @return [FloatArray]
     */
    override fun whc2cwh(arr: FloatArray): FloatArray {
        val temp = FloatArray(arr.size)
        var j = 0
        for (ch in 0 until 3) {
            for (i in ch until arr.size step 3) {
                temp[j] = arr[i]
                j++
            }
        }
        return temp
    }

    /**
     * 矩阵转置
     * @param matrix [Array]
     * @return [Array]
     */
    override fun transposeMatrix(matrix: Array<FloatArray>): Array<FloatArray> {
        val transMatrix = Array(matrix[0].size) { FloatArray(matrix.size) }
        for (i in matrix.indices) {
            for (j in matrix[0].indices) {
                transMatrix[j][i] = matrix[i][j]
            }
        }
        return transMatrix
    }

    /**
     * 获取数组中最大值的索远
     * @param arr [FloatArray]
     * @return [Int]
     */
    override fun maxIndex(arr: FloatArray): Int {
        var maxVal = Float.NEGATIVE_INFINITY
        var idx = 0
        for (i in arr.indices) {
            if (arr[i] > maxVal) {
                maxVal = arr[i]
                idx = i
            }
        }
        return idx
    }

    /**
     * 调整边界框（bounding box）从原始图像尺寸到目标图像尺寸。
     *
     * 该函数将一个边界框的坐标和尺寸从原始图像尺寸（`originalSize`）映射到目标图像尺寸（`targetSize`），
     * 并保持边界框在新尺寸图像中的正确位置和比例。此操作通常用于图像处理或计算机视觉任务中，
     * 例如当图像被缩放时需要调整边界框的位置和大小以适应新的图像尺寸。
     *
     * @param bbox 边界框的数组，格式为 [x, y, width, height]，其中 x 和 y 是边界框左上角的坐标，width 和 height 是边界框的宽度和高度。
     * @param originalSize 原始图像的尺寸，包含宽度和高度。
     * @param targetSize 目标图像的尺寸，包含宽度和高度。
     */
    override fun rescaleByPadding(bbox: FloatArray, originalSize: Size, targetSize: Size) {
        // 计算目标尺寸和原始尺寸之间的缩放比例
        val aspectRatio = minOf(targetSize.width / originalSize.width, targetSize.height / originalSize.height)
        // 计算调整后的图像新尺寸
        val newSize = Size(originalSize.width * aspectRatio, originalSize.height * aspectRatio)

        // 计算将图像居中对齐到目标尺寸所需的水平和垂直偏移量
        val xOffset = ((targetSize.width - newSize.width) / 2).toInt()
        val yOffset = ((targetSize.height - newSize.height) / 2).toInt()

        // 调整边界框的左上角坐标，缩放回原始尺寸下的位置
        bbox[0] = ((bbox[0] - xOffset) / aspectRatio).toFloat()
        bbox[1] = ((bbox[1] - yOffset) / aspectRatio).toFloat()

        // 调整边界框的宽度和高度
        bbox[2] = (bbox[2] / aspectRatio).toFloat()
        bbox[3] = (bbox[3] / aspectRatio).toFloat()
    }

    /**
     * 转换边界框为 xy xy
     * @param bbox [FloatArray]
     */
    override fun xywh2xyxy(bbox: FloatArray) {
        val x = bbox[0]
        val y = bbox[1]
        val w = bbox[2]
        val h = bbox[3]
        bbox[0] = x - w * 0.5f
        bbox[1] = y - h * 0.5f
        bbox[2] = x + w * 0.5f
        bbox[3] = y + h * 0.5f
    }
}