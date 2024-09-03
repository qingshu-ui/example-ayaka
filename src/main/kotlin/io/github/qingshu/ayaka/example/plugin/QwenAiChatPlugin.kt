package io.github.qingshu.ayaka.example.plugin

import io.github.qingshu.ayaka.dto.event.message.GroupMessageEvent
import io.github.qingshu.ayaka.dto.event.message.PrivateMessageEvent
import io.github.qingshu.ayaka.example.service.QwenService
import io.github.qingshu.ayaka.plugin.BotPlugin
import meteordevelopment.orbit.EventHandler
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

/**
 * Copyright (c) 2024 qingshu.
 * This file is part of the example-ayaka project.
 *
 * This project is licensed under the GPL-3.0 License.
 * See the LICENSE file for details.
 */
@Component
class QwenAiChatPlugin @Autowired constructor(
    private val qwenService: QwenService
) : BotPlugin {


    @EventHandler
    fun onPrivateMessage(event: PrivateMessageEvent) {
        val bot = event.bot
        val msg = event.rawMessage
        val userId = event.userId
        if (null != msg && null != bot && null != userId) {
            val respMsg = qwenService.chat(userId, msg)
            if (!checkAiResp(respMsg)) return
            bot.sendPrivateMsg(userId, respMsg, false)
        }
    }

    @EventHandler
    fun onGroupMessage(event: GroupMessageEvent) {
        val bot = event.bot
        val msg = event.rawMessage
        val userId = event.userId
        val groupId = event.groupId
        if (null != msg && null != bot && null != userId && null != groupId) {
            val atPattern = "\\[CQ:at,qq=${bot.selfId}(,[^]]*)?\\]".toRegex()
            if (null != atPattern.find(msg)) {
                val extractedMessage = msg.replace(atPattern, "").trim()
                if (extractedMessage.isEmpty()) return
                val respMsg = qwenService.chat(userId, extractedMessage)
                if (!checkAiResp(respMsg)) return
                val resp = bot.sendGroupMsg(groupId, "[CQ:at,qq=$userId] $respMsg", false)
                log.info("$resp")
            }
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(QwenAiChatPlugin::class.java)

        private fun checkAiResp(resp: String): Boolean {
            if ("" == resp) {
                log.warn("Unable to access SpringAI, response is null string")
                return false
            }
            return true
        }
    }
}