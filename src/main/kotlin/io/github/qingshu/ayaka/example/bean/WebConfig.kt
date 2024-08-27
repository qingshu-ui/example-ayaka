package io.github.qingshu.ayaka.example.bean

import io.github.qingshu.ayaka.example.config.CacheControlInterceptor
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

/**
 * Copyright (c) 2024 qingshu.
 * This file is part of the example-ayaka project.
 *
 * This project is licensed under the GPL-3.0 License.
 * See the LICENSE file for details.
 */
@Configuration
class WebConfig: WebMvcConfigurer {

    /**
     * 用于调试阶段禁止浏览器缓存
     */
    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(CacheControlInterceptor())
            .addPathPatterns("/")
    }
}