package io.github.qingshu.ayaka.example.filter

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
@WebFilter(urlPatterns = ["/*"])
class LoggingFilter : Filter {

    override fun doFilter(p0: ServletRequest?, p1: ServletResponse?, p2: FilterChain?) {
        val req = p0 as HttpServletRequest
        var visitIp = req.remoteAddr
        visitIp = if ("0:0:0:0:0:0:0:1" == visitIp) "127.0.0.1" else visitIp
        log.info("Request from: $visitIp")
        p2?.doFilter(p0, p1)
    }

    companion object {
        private val log = LoggerFactory.getLogger(LoggingFilter::class.java)
    }
}