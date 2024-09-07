package io.github.qingshu.ayaka.example.dto

import com.alibaba.fastjson2.annotation.JSONField

/**
 * Copyright (c) 2024 qingshu.
 * This file is part of the example-ayaka project.
 *
 * This project is licensed under the GPL-3.0 License.
 * See the LICENSE file for details.
 */
data class DouYinParseDTO(
    @JSONField(name = "aweme_detail")
    val detail: Detail,
) {
    data class Detail(
        val desc: String,
        val video: Video,
    ){
        data class Video(
            @JSONField(name = "play_addr")
            val play: Play,
            val cover: Cover,
        ){
            data class Play(
                @JSONField(name = "url_list")
                val urls: List<String>,
            )
            data class Cover(
                @JSONField(name = "url_list")
                val urls: List<String>,
            )
        }
    }
}