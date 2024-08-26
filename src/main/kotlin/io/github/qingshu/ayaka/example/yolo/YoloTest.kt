package io.github.qingshu.ayaka.example.yolo

import io.github.qingshu.ayaka.example.config.PropertiesReader
import org.opencv.imgcodecs.Imgcodecs
import kotlin.system.exitProcess

/**
 * Copyright (c) 2024 qingshu.
 * This file is part of the example-ayaka project.
 *
 * This project is licensed under the GPL-3.0 License.
 * See the LICENSE file for details.
 */
class YoloTest

fun testMain(args: Array<String>) {
    val reader = PropertiesReader()
    val classLoader = YoloTest::class.java.classLoader
    val modelPath = reader.get("slide_verification_model")
    val labelPath = reader.get("slide_verification_names")

    val imagePath = "C:\\Users\\17186\\IdeaProjects\\example-ayaka\\logs\\test.jpg"
    val model = YOLO.newInstance(modelPath, labelPath)
    val imgMat = Imgcodecs.imread(imagePath)
    if (imgMat.empty()) {
        println("Could not open image $imagePath")
        exitProcess(-1)
    }
    val detectObject = model.detectObject(imgMat)
    println(detectObject)
}