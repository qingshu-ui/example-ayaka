package io.github.qingshu.ayaka.example.utils

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