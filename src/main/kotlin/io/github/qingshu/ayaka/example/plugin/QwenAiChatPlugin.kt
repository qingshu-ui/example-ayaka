package io.github.qingshu.ayaka.example.plugin

import io.github.qingshu.ayaka.dto.event.message.GroupMessageEvent
import io.github.qingshu.ayaka.dto.event.message.MessageEvent
import io.github.qingshu.ayaka.dto.event.message.PrivateMessageEvent
import io.github.qingshu.ayaka.example.function.ScheduleTaskFunction
import io.github.qingshu.ayaka.example.function.ShowImageFunction
import io.github.qingshu.ayaka.example.service.QwenService
import io.github.qingshu.ayaka.example.yolo.YOLO
import io.github.qingshu.ayaka.plugin.BotPlugin
import io.github.qingshu.ayaka.utils.MsgUtils
import meteordevelopment.orbit.EventHandler
import meteordevelopment.orbit.EventPriority
import org.slf4j.LoggerFactory
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
@Component
class QwenAiChatPlugin @Autowired constructor(
    private val qwenService: QwenService,
    @Qualifier("ayakaTaskScheduler") private val task: ThreadPoolTaskScheduler,
    private val sliderModel: YOLO
) : BotPlugin {


    @EventHandler(priority = EventPriority.HIGH)
    fun onPrivateMessage(event: PrivateMessageEvent) {
        val bot = event.bot
        val msg = event.rawMessage
        val userId = event.userId
        if (null != msg && null != bot && null != userId) {
            val options = createAiOptions(event, task = task)
            val respMsg = qwenService.chat(userId, msg, options)
            if (!checkAiResp(respMsg)) return
            bot.sendPrivateMsg(userId, respMsg, false)
            event.cancel()
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    fun onGroupMessage(event: GroupMessageEvent) {
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
                if (!checkAiResp(respMsg)) return
                val resp = bot.sendGroupMsg(groupId, MsgUtils.builder().reply(msgId!!).text(respMsg).build(), false)
                log.info("$resp")
                event.cancel()
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
                    FunctionCallbackWrapper.builder(
                        ShowImageFunction(event = event)
                    )
                        .withName("showImageWithPath")
                        .withDescription(
                            "将传入的图片路径或者网络地址展示出来"
                        )
                        .build()
                )
            )
            .withFunction("detectObjectInImage")
        return options.build()
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