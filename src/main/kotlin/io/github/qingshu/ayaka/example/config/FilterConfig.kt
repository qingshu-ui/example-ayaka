package io.github.qingshu.ayaka.example.config

import io.github.qingshu.ayaka.example.filter.InvalidRequestFilter
import io.github.qingshu.ayaka.example.filter.LoggingFilter
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * Copyright (c) 2024 qingshu.
 * This file is part of the example-ayaka project.
 *
 * This project is licensed under the GPL-3.0 License.
 * See the LICENSE file for details.
 */
@Configuration
class FilterConfig {

    @Bean
    fun loggingFilter(): FilterRegistrationBean<LoggingFilter> {
        val registration = FilterRegistrationBean<LoggingFilter>()
        registration.filter = LoggingFilter()
        registration.order = 1
        return registration
    }

    @Bean
    fun invalidReqFilter(): FilterRegistrationBean<InvalidRequestFilter> {
        val registration = FilterRegistrationBean<InvalidRequestFilter>()
        registration.filter = InvalidRequestFilter()
        registration.order = 2
        return registration
    }
}