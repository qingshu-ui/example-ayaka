package io.github.qingshu.ayaka.example.filter

import io.github.qingshu.ayaka.example.annotation.Slf4j
import io.github.qingshu.ayaka.example.annotation.Slf4j.Companion.log
import jakarta.servlet.Filter
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.annotation.WebFilter
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory

/**
 * Copyright (c) 2024 qingshu.
 * This file is part of the example-ayaka project.
 *
 * This project is licensed under the GPL-3.0 License.
 * See the LICENSE file for details.
 */
@Slf4j
@WebFilter(urlPatterns = ["/*"])
class InvalidRequestFilter : Filter {

    override fun doFilter(p0: ServletRequest?, p1: ServletResponse?, p2: FilterChain?) {
        if (p1 is ServletRequest) {
            val httpRequest = p1 as HttpServletRequest
            val reqMethod = httpRequest.method
            if (!reqMethod.matches("[a-zA-Z]+".toRegex())) {
                p1.writer.apply {
                    write("Invalid HTTP Request")
                    flush()
                    close()
                }
                log.warn("Invalid HTTP Request closed from ${httpRequest.remoteAddr}")
                return
            }
        }
        p2?.doFilter(p0, p1)
    }
}