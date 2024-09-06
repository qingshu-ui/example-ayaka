package io.github.qingshu.ayaka.example.function

import com.fasterxml.jackson.annotation.JsonClassDescription
import io.github.qingshu.ayaka.dto.event.message.GroupMessageEvent
import io.github.qingshu.ayaka.dto.event.message.MessageEvent
import io.github.qingshu.ayaka.dto.event.message.PrivateMessageEvent
import io.github.qingshu.ayaka.utils.MsgUtils
import org.slf4j.LoggerFactory
import java.util.function.Function

/**
 * Copyright (c) 2024 qingshu.
 * This file is part of the example-ayaka project.
 *
 * This project is licensed under the GPL-3.0 License.
 * See the LICENSE file for details.
 */
class ShowImageFunction(
    private val event: MessageEvent
) : Function<ShowImageFunction.Request, Boolean> {

    @JsonClassDescription(
        "Note: 'path' field required image path or http url." +
        "For example, 'C:\\Users\\17186\\IdeaProjects\\example-ayaka\\detection\\38ee3351-e15c-46a2-9d02-0d1a7411fe6c.png'"
    )
    data class Request(val path: String = "")
    override fun apply(t: Request): Boolean {
        log.info("ShowImageFunction.apply start")
        val bot = event.bot!!
        val userId = event.userId!!
        val msgId = when (event.messageType) {
            "group" -> (event as GroupMessageEvent).messageId ?: 0
            "private" -> (event as PrivateMessageEvent).messageId ?: 0
            else -> 0
        }
        val groupId = when (event.messageType) {
            "group" -> (event as GroupMessageEvent).groupId ?: 0
            else -> 0L
        }

        val replyMsg = MsgUtils.builder()
            .reply(msgId)
            .img(t.path)
            .build()

        if (0L != groupId) {
            val echo = bot.sendGroupMsg(groupId, replyMsg, false)
            log.info("ShowImageFunction.apply complete")
            return echo.status.equals("ok")
        }
        val echo = bot.sendPrivateMsg(userId, replyMsg, false)
        log.info("ShowImageFunction.apply complete")
        return echo.status.equals("ok")
    }

    companion object {
        private val log = LoggerFactory.getLogger(ShowImageFunction::class.java)
    }
}