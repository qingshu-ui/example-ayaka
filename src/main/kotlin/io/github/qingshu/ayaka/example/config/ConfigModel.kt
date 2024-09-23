package io.github.qingshu.ayaka.example.config

import com.fasterxml.jackson.annotation.JsonProperty

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
    @JsonProperty("base") val base: Base,
    @JsonProperty("plugins") val plugins: Plugins,
) {
    data class Base(
        @JsonProperty("adminList") val adminList: List<Long>,
        @JsonProperty("nickName") val nickName: String,
    )

    data class Plugins(
        @JsonProperty("driftBottle") val driftBottle: DriftBottle,
        @JsonProperty("repeat") val repeat: Repeat,
        @JsonProperty("roulette") val roulette: Roulette,
        @JsonProperty("randomVideo") val randomVideo: RandomVideo
    ) {
        data class DriftBottle(
            @JsonProperty("cd") val cd: Int,
        )

        data class Repeat(
            @JsonProperty("waitTime") val waitTime: Int,
            @JsonProperty("thresholdValue") val thresholdValue: Int,
        )

        data class Roulette(
            @JsonProperty("timeout") val timeout: Int,
            @JsonProperty("maxMuteTime") val maxMuteTime: Int,
        )

        data class RandomVideo(
            @JsonProperty("path") val path: String,
        )
    }
}