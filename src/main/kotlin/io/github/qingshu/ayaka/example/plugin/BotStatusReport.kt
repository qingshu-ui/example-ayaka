package io.github.qingshu.ayaka.example.plugin

import io.github.qingshu.ayaka.bot.BotFactory
import io.github.qingshu.ayaka.bot.BotSessionFactory
import io.github.qingshu.ayaka.example.config.EAConfig
import io.github.qingshu.ayaka.utils.MsgUtils
import jakarta.annotation.PreDestroy
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Copyright (c) 2024 qingshu.
 * This file is part of the example-ayaka project.
 *
 * This project is licensed under the APGL-3.0 License.
 * See the LICENSE file for details.
 */
@Component
class BotStatusReport(
    botFactory: BotFactory,
    sessionFactory: BotSessionFactory,
) {

    private val cfg = EAConfig.base
    private val botSession = sessionFactory.createSession("localhost")
    private val bot = botFactory.createBot(cfg.selfId, botSession)

    @EventListener(ApplicationReadyEvent::class)
    fun onStarted() = kotlin.runCatching {
        val completedTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))
        val msg = MsgUtils.builder()
            .text("Bot is started on: $completedTime")
            .build()
        cfg.adminList.forEach {
            bot.sendPrivateMsg(it, msg)
        }
    }

    @PreDestroy
    fun onDestroy() = kotlin.runCatching {
        val completedTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))
        val msg = MsgUtils.builder()
            .text("Bot is closed at: $completedTime")
            .build()
        cfg.adminList.forEach {
            bot.sendPrivateMsg(it, msg)
        }
    }
}