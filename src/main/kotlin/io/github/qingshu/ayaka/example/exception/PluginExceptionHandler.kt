package io.github.qingshu.ayaka.example.exception

import io.github.qingshu.ayaka.dto.event.message.AnyMessageEvent
import io.github.qingshu.ayaka.dto.event.message.GroupMessageEvent
import io.github.qingshu.ayaka.dto.event.message.PrivateMessageEvent
import io.github.qingshu.ayaka.example.annotation.Slf4j
import io.github.qingshu.ayaka.example.annotation.Slf4j.Companion.log

/**
 * Copyright (c) 2024 https://github.com/MisakaTAT/Yuri-Kotlin.
 * This file is part of the https://github.com/MisakaTAT/Yuri-Kotlin project.
 *
 * This file is licensed under the AGPL-3.0 License.
 * See the LICENSE file for details.
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
            is AnyMessageEvent -> event.let { it.bot?.sendMsg(it, message) }
            is GroupMessageEvent -> event.let { it.bot?.sendGroupMsg(it.groupId!!, message) }
            is PrivateMessageEvent -> event.let { it.bot?.sendPrivateMsg(it.userId!!, message) }
        }
    }

}