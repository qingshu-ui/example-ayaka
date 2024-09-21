package io.github.qingshu.ayaka.example.utils

/**
 * Copyright (c) 2024 https://github.com/MisakaTAT/Yuri-Kotlin.
 * This file is part of the https://github.com/MisakaTAT/Yuri-Kotlin project.
 *
 * This file is licensed under the AGPL-3.0 License.
 * See the LICENSE file for details.
 */
object Regex {
    const val DOU_YIN_SHORT_URL = "(?<url>(?:https?:\\/\\/)?v\\.douyin\\.com\\/\\w+)"
    const val DOU_YIN_REAL_URL_ID = "/video/(?<id>\\d+)"
    const val DRIFT_BOTTLE = "^[丢扔]漂流瓶\\s?([\\s\\S]+)\$|^[捡捞]漂流瓶\$|^跳海\$|^查漂流瓶\\s?(.*)\$"
}