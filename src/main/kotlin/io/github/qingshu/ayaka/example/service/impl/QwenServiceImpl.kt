package io.github.qingshu.ayaka.example.service.impl

import io.github.qingshu.ayaka.example.service.QwenService
import org.springframework.ai.chat.messages.AssistantMessage
import org.springframework.ai.chat.messages.Message
import org.springframework.ai.chat.messages.SystemMessage
import org.springframework.ai.chat.messages.UserMessage
import org.springframework.ai.chat.prompt.Prompt
import org.springframework.ai.openai.OpenAiChatModel
import org.springframework.ai.openai.OpenAiChatOptions
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 * Copyright (c) 2024 qingshu.
 * This file is part of the example-ayaka project.
 *
 * This project is licensed under the GPL-3.0 License.
 * See the LICENSE file for details.
 */
@Service
class QwenServiceImpl @Autowired constructor(
    private val model: OpenAiChatModel
) : QwenService {

    private val userConversations = mutableMapOf<Long, MutableList<Message>>()

    @Synchronized
    override fun chat(user: Long, message: String): String {
        val options = OpenAiChatOptions.builder()
            .withModel("qwen-max-0428")
            .build()
        return this.chat(user, message, options)
    }

    override fun chat(user: Long, message: String, options: OpenAiChatOptions): String {
        val conversationHistory = userConversations.getOrPut(user) { mutableListOf() }

        if (conversationHistory.isEmpty() || conversationHistory.first() !is SystemMessage) {
            val systemMessage =
                SystemMessage("你是图片检测模型，你只需要调用函数处理图片和展示图片，你只能回复至多2个字。")
            conversationHistory.add(0, systemMessage)
        }
        val userMessage = UserMessage(message)
        conversationHistory.add(userMessage)

        while (conversationHistory.size > 11) {
            val firstUserMessageIndex = conversationHistory.indexOfFirst { it is UserMessage || it is AssistantMessage }
            if (firstUserMessageIndex != -1) {
                if (firstUserMessageIndex < conversationHistory.size && conversationHistory[firstUserMessageIndex] is UserMessage && conversationHistory[firstUserMessageIndex + 1] is AssistantMessage) {
                    conversationHistory.removeAt(firstUserMessageIndex + 1)
                }
                conversationHistory.removeAt(firstUserMessageIndex)
            }
        }

        lateinit var assistantMessage: AssistantMessage
        try {
            assistantMessage = model.call(Prompt(conversationHistory, options)).result.output
        } catch (e: Exception) {
            println(e.message)
        }
        conversationHistory.add(assistantMessage)
        return assistantMessage.content
    }
}