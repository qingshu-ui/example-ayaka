package io.github.qingshu.ayaka.example.plugin

import io.github.qingshu.ayaka.annotation.MessageHandlerFilter
import io.github.qingshu.ayaka.bot.BotContainer
import io.github.qingshu.ayaka.dto.event.message.GroupMessageEvent
import io.github.qingshu.ayaka.dto.event.message.PrivateMessageEvent
import io.github.qingshu.ayaka.example.annotation.Slf4j
import io.github.qingshu.ayaka.example.annotation.Slf4j.Companion.log
import io.github.qingshu.ayaka.plugin.BotPlugin
import io.github.qingshu.ayaka.utils.MsgUtils
import meteordevelopment.orbit.EventHandler
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
@Slf4j
@Component
class ScheduleTask : BotPlugin {

    @Autowired
    lateinit var botContainer: BotContainer

    @EventHandler
    @MessageHandlerFilter(cmd = "like")
    fun handler(event: PrivateMessageEvent) {
        val bot = event.bot!!
        val userId = event.userId!!
        bot.sendPrivateMsg(
            userId = userId,
            msg = MsgUtils.builder().reply(event.messageId!!).text("欧克呀，宝").build()
        )
        val result = bot.sendLike(userId)
        log.info("Action like done: {}", result)
    }

    @EventHandler
    @MessageHandlerFilter(cmd = "sign")
    fun handler(event: GroupMessageEvent) {
        val bot = event.bot!!
        val echo = bot.sendGroupSign(event.groupId!!)
        log.info("Action sign done: {}", echo)
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
}