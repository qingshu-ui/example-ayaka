package io.github.qingshu.ayaka.example.plugin

import io.github.qingshu.ayaka.bot.BotContainer
import io.github.qingshu.ayaka.dto.event.message.PrivateMessageEvent
import io.github.qingshu.ayaka.plugin.BotPlugin
import io.github.qingshu.ayaka.plugin.MyPlugin
import meteordevelopment.orbit.EventHandler
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

/**
 * Copyright (c) 2024 qingshu.
 * This file is part of the example-ayaka project.
 *
 * This project is licensed under the GPL-3.0 License.
 * See the LICENSE file for details.
 */
@Component("secondMyPlugin")
class MyPlugin : BotPlugin {

    @Autowired
    lateinit var botContainer: BotContainer

    @EventHandler
    fun onPrivate(event: PrivateMessageEvent) {
        val bot = event.bot!!
        val msg = event.rawMessage
        if (msg == "like") {
            val result = bot.sendLike(event.userId!!)
            log.info("Action like done: {}", result)
        }
    }

    @Scheduled(cron = "5 0 0 * * ?")
    fun friendLike() {
        val userId = 1718692748L
        botContainer.bots.forEach {
            val bot = it.value
            val rel = bot.sendLike(userId)
            val result = bot.sendPrivateMsg(userId, "点赞：${rel.status}", false)
            log.info("Daily like: {}", result)
        }
    }

    @Scheduled(cron = "10 0 0 * * ?")
    fun groupSign() {
        val groupList = listOf(
            244427991L, 335783090L, 737472779L
        )
        val userId = 1718692748L
        botContainer.bots.forEach { it ->
            val bot = it.value
            groupList.forEach {
                val rel = bot.sendGroupSign(it)
                val result = bot.sendPrivateMsg(userId, "$it group sign: ${rel.status}", false)
                log.info("Daily sign: {}", rel)
            }
        }
    }


    companion object {
        private val log = LoggerFactory.getLogger(MyPlugin::class.java)
    }
}