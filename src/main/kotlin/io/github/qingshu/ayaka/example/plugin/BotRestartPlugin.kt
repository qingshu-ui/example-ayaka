package io.github.qingshu.ayaka.example.plugin

import io.github.qingshu.ayaka.bot.BotFactory
import io.github.qingshu.ayaka.bot.BotSessionFactory
import io.github.qingshu.ayaka.example.config.EAConfig
import io.github.qingshu.ayaka.utils.MsgUtils
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
class BotRestartPlugin(
    private val botFactory: BotFactory,
    private val sessionFactory: BotSessionFactory,
) {

    private val cfg = EAConfig.base

    @EventListener(ApplicationReadyEvent::class)
    fun onApplicationReady() {
        val botSession = sessionFactory.createSession("localhost")
        val bot = botFactory.createBot(cfg.selfId, botSession)
        val completedTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))
        val msg = MsgUtils.builder()
            .text("Bot is restarted in: $completedTime")
            .build()
        cfg.adminList.forEach {
            bot.sendPrivateMsg(it, msg)
        }
    }
}