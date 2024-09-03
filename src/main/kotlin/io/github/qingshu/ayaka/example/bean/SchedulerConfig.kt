package io.github.qingshu.ayaka.example.bean

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler

/**
 * Copyright (c) 2024 qingshu.
 * This file is part of the example-ayaka project.
 *
 * This project is licensed under the GPL-3.0 License.
 * See the LICENSE file for details.
 */
@Configuration
class SchedulerConfig {

    @Bean
    @ConditionalOnMissingBean
    fun taskScheduler(): ThreadPoolTaskScheduler {
        return ThreadPoolTaskScheduler().apply {
            poolSize = 10
            setThreadNamePrefix("Scheduled-task")
        }
    }
}