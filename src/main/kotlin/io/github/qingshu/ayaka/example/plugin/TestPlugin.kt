package io.github.qingshu.ayaka.example.plugin

import io.github.qingshu.ayaka.annotation.MessageHandlerFilter
import io.github.qingshu.ayaka.dto.event.message.PrivateMessageEvent
import io.github.qingshu.ayaka.example.annotation.Slf4j
import io.github.qingshu.ayaka.example.annotation.Slf4j.Companion.log
import io.github.qingshu.ayaka.example.service.DouYinVideoService
import io.github.qingshu.ayaka.plugin.BotPlugin
import meteordevelopment.orbit.EventHandler
import org.springframework.stereotype.Component

/**
 * Copyright (c) 2024 qingshu.
 * This file is part of the example-ayaka project.
 *
 * This project is licensed under the AGPL-3.0 License.
 * See the LICENSE file for details.
 */
@Slf4j
@Component
class TestPlugin(
    private val service: DouYinVideoService,
):BotPlugin {

    @EventHandler
    @MessageHandlerFilter(cmd = "test")
    fun handler(e: PrivateMessageEvent) {
        val result = service.requiredUpdateInfo(50)
        log.info("${result.size}")
    }
}