package io.github.qingshu.ayaka.example.plugin

import io.github.qingshu.ayaka.dto.event.message.GroupMessageEvent
import io.github.qingshu.ayaka.dto.event.message.MessageEvent
import io.github.qingshu.ayaka.dto.event.message.PrivateMessageEvent
import io.github.qingshu.ayaka.example.annotation.Slf4j
import io.github.qingshu.ayaka.example.annotation.Slf4j.Companion.log
import io.github.qingshu.ayaka.example.function.ScheduleTaskFunction
import io.github.qingshu.ayaka.example.service.QwenService
import io.github.qingshu.ayaka.plugin.BotPlugin
import io.github.qingshu.ayaka.utils.MsgUtils
import org.springframework.ai.model.function.FunctionCallbackWrapper
import org.springframework.ai.openai.OpenAiChatOptions
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler
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
class QwenAiChat @Autowired constructor(
    private val qwenService: QwenService,
    @Qualifier("ayakaTaskScheduler") private val task: ThreadPoolTaskScheduler,
) : BotPlugin {

    // @EventHandler
    fun handler(event: PrivateMessageEvent) {
        val bot = event.bot
        val msg = event.rawMessage
        val userId = event.userId
        if (null != msg && null != bot && null != userId) {
            val options = createAiOptions(event, task = task)
            val respMsg = qwenService.chat(userId, msg, options)
            bot.sendPrivateMsg(userId, respMsg, false)
        }
    }

    // @EventHandler()
    fun handler(event: GroupMessageEvent) {
        val bot = event.bot
        val msg = event.rawMessage
        val msgId = event.messageId
        val userId = event.userId
        val groupId = event.groupId
        if (null != msg && null != bot && null != userId && null != groupId) {
            val atPattern = "\\[CQ:at,qq=${bot.selfId}(,[^]]*)?\\]".toRegex()
            if (null != atPattern.find(msg)) {
                val extractedMessage = msg.replace(atPattern, "").trim()
                if (extractedMessage.isEmpty()) return

                val options = createAiOptions(event, task)

                val respMsg = qwenService.chat(userId, extractedMessage, options)
                val resp = bot.sendGroupMsg(groupId, MsgUtils.builder().reply(msgId!!).text(respMsg).build(), false)
                log.info("$resp")
            }
        }
    }

    fun createAiOptions(event: MessageEvent, task: ThreadPoolTaskScheduler): OpenAiChatOptions {
        val bot = event.bot!!
        val userId = event.userId!!
        var groupId = 0L
        if (event is GroupMessageEvent) {
            groupId = event.groupId ?: 0L
        }
        val options = OpenAiChatOptions.builder()
            .withModel("qwen-max")
            .withFunctionCallbacks(
                listOf(
                    FunctionCallbackWrapper.builder(
                        ScheduleTaskFunction(bot, userId, groupId, task)
                    )
                        .withName("SetScheduleTask")
                        .withDescription("可以通过这个函数设置定时的任务，比如闹钟，提醒，等等")
                        .build(),
                )
            )
            .withFunction("detectObjectInImage")
        return options.build()
    }
}