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
    fun nonMaxSuppression(bboxList: ArrayList<FloatArray>, iou: Float): ArrayList<FloatArray>

}