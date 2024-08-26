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

fun main(args: Array<String>) {
    val reader = PropertiesReader()
    val classLoader = YoloTest::class.java.classLoader
    val modelPath = classLoader.getResource(reader.get("slide_verification_model"))?.path?.removePrefix("/")
    val labelPath = classLoader.getResource(reader.get("slide_verification_names"))?.path?.removePrefix("/")

    val imagePath = "C:\\Users\\17186\\IdeaProjects\\example-ayaka\\logs\\test.jpg"
    val model = YOLO(modelPath!!, labelPath!!)
    val imgMat = Imgcodecs.imread(imagePath)
    if (imgMat.empty()) {
        println("Could not open image $imagePath")
        exitProcess(-1)
    }
    val detectObject = model.detectObject(imgMat)
    println(detectObject)
}