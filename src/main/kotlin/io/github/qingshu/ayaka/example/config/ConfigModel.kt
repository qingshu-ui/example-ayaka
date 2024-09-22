package io.github.qingshu.ayaka.example.config

/**
 * This file is part of the Yuri-Kotlin project:
 * https://github.com/MisakaTAT/Yuri-Kotlin
 *
 * Original Copyright (c) 2024 MisakaTAT
 * Licensed under the APGL-3.0 License. You may obtain a copy of the License at:
 *
 *     https://github.com/MisakaTAT/Yuri-Kotlin/blob/main/LICENSE
 *
 * Modifications:
 * - Modified by qingshu on 2024
 * - ignore
 *
 * This file is licensed under the same APGL-3.0 License.
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
        val repeat: Repeat,
        val roulette: Roulette,
    ) {
        data class DriftBottle(
            val cd: Int,
        )

        data class Repeat(
            val waitTime: Int,
            val thresholdValue: Int,
        )

        data class Roulette(
            val timeout: Int,
            val maxMuteTime: Int,
        )
    }
}