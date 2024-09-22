package io.github.qingshu.ayaka.example.utils

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
object Regex {
    const val DOU_YIN_SHORT_URL = "(?<url>(?:https?:\\/\\/)?v\\.douyin\\.com\\/\\w+)"
    const val DOU_YIN_REAL_URL_ID = "/video/(?<id>\\d+)"
    const val DRIFT_BOTTLE = "^[丢扔]漂流瓶\\s?([\\s\\S]+)\$|^[捡捞]漂流瓶\$|^跳海\$|^查漂流瓶\\s?(.*)\$"
    const val ROULETTE = "^切换轮盘模式\$|^开始轮盘(\\s[1-6])?\$|^开枪\$"
}