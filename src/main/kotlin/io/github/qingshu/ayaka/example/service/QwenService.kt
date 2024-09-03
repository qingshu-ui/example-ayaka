package io.github.qingshu.ayaka.example.service

import org.springframework.ai.openai.OpenAiChatOptions

/**
 * Copyright (c) 2024 qingshu.
 * This file is part of the example-ayaka project.
 *
 * This project is licensed under the GPL-3.0 License.
 * See the LICENSE file for details.
 */
interface QwenService {

    fun chat(user: Long, message: String): String

    fun chat(user: Long, message: String, options: OpenAiChatOptions): String
}