package io.github.qingshu.ayaka.example.service.impl

import io.github.qingshu.ayaka.example.service.SliderInferenceService
import io.github.qingshu.ayaka.example.yolo.Detection
import java.util.function.Function

/**
 * Copyright (c) 2024 qingshu.
 * This file is part of the example-ayaka project.
 *
 * This project is licensed under the GPL-3.0 License.
 * See the LICENSE file for details.
 */
class SliderInferenceServiceImpl: SliderInferenceService, Function<String, Detection>{

    override fun apply(t: String): Detection {
        TODO("Not yet implemented")
    }
}