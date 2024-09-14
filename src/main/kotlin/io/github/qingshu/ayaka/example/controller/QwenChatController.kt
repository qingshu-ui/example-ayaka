package io.github.qingshu.ayaka.example.controller

import io.github.qingshu.ayaka.example.service.QwenService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

/**
 * Copyright (c) 2024 qingshu.
 * This file is part of the example-ayaka project.
 *
 * This project is licensed under the GPL-3.0 License.
 * See the LICENSE file for details.
 */
@RestController
class QwenChatController @Autowired constructor(
    private val qwenService: QwenService
) {

    @RequestMapping("/api/chat")
    fun chat(@RequestParam("id") userId: Long, @RequestParam("msg") message: String): String {
        return qwenService.chat(userId, message)
    }
}