package io.github.qingshu.ayaka.example.config

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.web.servlet.HandlerInterceptor

/**
 * Copyright (c) 2024 qingshu.
 * This file is part of the example-ayaka project.
 *
 * This project is licensed under the GPL-3.0 License.
 * See the LICENSE file for details.
 */
class CacheControlInterceptor : HandlerInterceptor {

    override fun afterCompletion(
        request: HttpServletRequest, response: HttpServletResponse, handler: Any, ex: Exception?
    ) {
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate")
        response.setHeader("Pragma", "no-cache")
        response.setDateHeader("Expires", 0)
    }
}