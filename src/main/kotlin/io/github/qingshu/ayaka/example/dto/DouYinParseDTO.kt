package io.github.qingshu.ayaka.example.dto

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
data class DouYinParseDTO(
    @JsonProperty("aweme_detail")
    val detail: Detail,
) {
    data class Detail(
        val desc: String,
        val video: Video,
    ){
        data class Video(
            @JsonProperty("play_addr")
            val play: Play,
            val cover: Cover,
        ){
            data class Play(
                @JsonProperty("url_list")
                val urls: List<String>,
            )
            data class Cover(
                @JsonProperty("url_list")
                val urls: List<String>,
            )
        }
    }
}