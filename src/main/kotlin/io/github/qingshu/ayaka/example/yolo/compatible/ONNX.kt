package io.github.qingshu.ayaka.example.yolo.compatible

import org.opencv.core.Mat
import org.opencv.core.Size

/**
 * Copyright (c) 2024 qingshu.
 * This file is part of the example-ayaka project.
 *
 * This project is licensed under the GPL-3.0 License.
 * See the LICENSE file for details.
 */
interface ONNX {

    /**
     * 以填充的形式调整图像，以符合目标大小
     * @param mat [Mat]
     * @param targetSize [Size]
     * @return [Mat]
     */
    fun scaleByPadding(mat: Mat, targetSize: Size): Mat

    /**
     * 将 宽、高、通道 转换为 通道、宽、高
     * whc to cwh
     * @param arr [FloatArray]
     * @return [FloatArray]
     */
    fun whc2cwh(arr: FloatArray): FloatArray

    /**
     * 矩阵转置
     * @param matrix [Array]
     * @return [Array]
     */
    fun transposeMatrix(matrix: Array<FloatArray>): Array<FloatArray>

    /**
     * 获取数组中最大值的索远
     * @param arr [FloatArray]
     * @return [Int]
     */
    fun maxIndex(arr: FloatArray): Int

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
    fun rescaleByPadding(bbox: FloatArray, originalSize: Size, targetSize: Size)

    /**
     * 转换边界框为 xy xy
     * @param bbox [FloatArray]
     */
    fun xywh2xyxy(bbox: FloatArray)

    /**
     * 非极大值抑制
     * @param bboxList [ArrayList]
     * @param iou [Float] 交并比（Intersection over Union, IoU）
     * @return [ArrayList]
     */
    fun nonMaxSuppression(bboxList: ArrayList<FloatArray>, iou: Float): ArrayList<FloatArray> {
        val bestBoxes = ArrayList<FloatArray>()
        bboxList.sortWith(compareBy { it[4] })
        while (bboxList.isNotEmpty()) {
            val bestBox = bboxList.removeAt(bboxList.size - 1)
            bestBoxes.add(bestBox)
            bboxList.removeAll { computeIou(it, bestBox) >= iou }
        }
        return bestBoxes
    }

    /**
     * 在目标检测任务中，IoU用来衡量预测边界框与真实边界框之间的重叠程度。
     * 高的IoU意味着预测框与真实框的重叠部分更大，从而可以更准确地定位对象。
     */
    private fun computeIou(box1: FloatArray, box2: FloatArray): Float {
        // 计算第一个边界框的面积: (x_max - x_min) * (y_max - y_min)
        val area1 = (box1[2] - box1[0]) * (box1[3] - box1[1])
        // 计算第二个边界框的面积: (x_max - x_min) * (y_max - y_min)
        val area2 = (box2[2] - box2[0]) * (box2[3] - box2[1])

        // 计算相交区域的左边界: 取两个边界框左边界中的最大值
        val left = maxOf(box1[0], box2[0])
        // 计算相交区域的上边界: 取两个边界框上边界中的最大值
        val top = maxOf(box1[1], box2[1])
        // 计算相交区域的右边界: 取两个边界框右边界中的最小值
        val right = minOf(box1[2], box2[2])
        // 计算相交区域的下边界: 取两个边界框下边界中的最小值
        val bottom = minOf(box1[3], box2[3])

        // 计算相交区域的面积: 如果相交区域不存在，面积为0
        // maxOf(right - left, 0f) 和 maxOf(bottom - top, 0f) 确保面积不为负
        val interArea = maxOf(right - left, 0f) * maxOf(bottom - top, 0f)
        // 计算两个边界框的并集面积: area1 + area2 - interArea
        val unionArea = area1 + area2 - interArea

        // 计算交并比 (IoU): 相交面积除以并集面积
        // 如果 unionArea 为0，返回一个非常小的数 (1e-8f) 来避免除以零
        return maxOf(interArea / unionArea, 1e-8f)
    }
}