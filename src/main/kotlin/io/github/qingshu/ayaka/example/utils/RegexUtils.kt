package io.github.qingshu.ayaka.example.utils

/**
 * Copyright (c) 2024 https://github.com/MisakaTAT/Yuri-Kotlin.
 * This file is part of the https://github.com/MisakaTAT/Yuri-Kotlin project.
 *
 * This file is licensed under the AGPL-3.0 License.
 * See the LICENSE file for details.
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