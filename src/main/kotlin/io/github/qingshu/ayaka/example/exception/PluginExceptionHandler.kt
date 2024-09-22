package io.github.qingshu.ayaka.example.exception

import io.github.qingshu.ayaka.dto.event.message.AnyMessageEvent
import io.github.qingshu.ayaka.dto.event.message.GroupMessageEvent
import io.github.qingshu.ayaka.dto.event.message.PrivateMessageEvent
import io.github.qingshu.ayaka.example.annotation.Slf4j
import io.github.qingshu.ayaka.example.annotation.Slf4j.Companion.log

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
@Slf4j
object PluginExceptionHandler {

    fun with(event: Any, block: () -> Unit) {
        try {
            block()
        } catch (e: Exception) {
            push(event, "ERROR: ${e.message}")
            log.error(e.stackTrace.first().className, e)
        }
    }

    private fun push(event: Any, message: String) {
        when (event) {
            is AnyMessageEvent -> event.let { it.bot.sendMsg(it, message) }
            is GroupMessageEvent -> event.let { it.bot.sendGroupMsg(it.groupId, message) }
            is PrivateMessageEvent -> event.let { it.bot.sendPrivateMsg(it.userId, message) }
        }
    }

}