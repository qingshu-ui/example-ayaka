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
object RegexUtils {

    fun check(str: String, regex: String): Boolean{
        val pattern = regex.toRegex()
        return pattern.containsMatchIn(str)
    }

    fun group(group: String, text: String, regex: String): String {
        val match = regex.toRegex().find(text) ?: return ""
        return match.groups[group]?.value ?: ""
    }
}