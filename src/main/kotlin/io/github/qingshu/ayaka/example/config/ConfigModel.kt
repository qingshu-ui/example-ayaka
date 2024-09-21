package io.github.qingshu.ayaka.example.config

/**
 * Copyright (c) 2024 https://github.com/MisakaTAT/Yuri-Kotlin.
 * This file is part of the https://github.com/MisakaTAT/Yuri-Kotlin project.
 *
 * This file is licensed under the AGPL-3.0 License.
 * See the LICENSE file for details.
 */
data class ConfigModel(
    val base: Base,
    val plugins: Plugins,
) {
    data class Base(
        val adminList: List<Long>,
        val nickName: String,
    )

    data class Plugins(
        val driftBottle: DriftBottle,
    ) {
        data class DriftBottle(
            val cd: Int,
        )
    }
}