package io.github.qingshu.ayaka.example.utils

object Regex {
    const val DOU_YIN_SHORT_URL = "(?<url>(?:https?:\\/\\/)?v\\.douyin\\.com\\/\\w+)"
    const val DOU_YIN_REAL_URL_ID = "/video/(?<id>\\d+)"
    const val DRIFT_BOTTLE = "^[丢扔]漂流瓶\\s?([\\s\\S]+)\$|^[捡捞]漂流瓶\$|^跳海\$|^查漂流瓶\\s?(.*)\$"
}