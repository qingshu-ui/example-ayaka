package io.github.qingshu.ayaka.example.plugin

import io.github.qingshu.ayaka.annotation.MessageHandlerFilter
import io.github.qingshu.ayaka.dto.constant.AtEnum
import io.github.qingshu.ayaka.dto.event.message.GroupMessageEvent
import io.github.qingshu.ayaka.example.annotation.Slf4j
import io.github.qingshu.ayaka.example.annotation.Slf4j.Companion.log
import io.github.qingshu.ayaka.example.config.EAConfig
import io.github.qingshu.ayaka.plugin.BotPlugin
import io.github.qingshu.ayaka.utils.MsgUtils
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
class AdminCommand : BotPlugin {

    private val cfg = EAConfig.base

    @EventHandler
    @MessageHandlerFilter(cmd = "退群", at = AtEnum.NEED)
    fun groupQuitCmd(e: GroupMessageEvent) {
        val bot = e.bot
        val userId = e.userId
        if (userId in cfg.adminList) {
            bot.sendGroupMsg(e.groupId, MsgUtils.builder().reply(e.messageId).text("收到").build())
            val echo = bot.setGroupLeave(e.groupId, false)
            log.info("Executed cmd: {}", echo)
        }
    }
}