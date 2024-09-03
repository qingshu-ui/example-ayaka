package io.github.qingshu.ayaka.example.service.impl

import com.fasterxml.jackson.annotation.JsonClassDescription
import io.github.qingshu.ayaka.bot.Bot
import io.github.qingshu.ayaka.example.service.ScheduleTaskService
import io.github.qingshu.ayaka.utils.MsgUtils
import org.slf4j.LoggerFactory
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler
import java.time.Instant
import java.util.function.Function

/**
 * Copyright (c) 2024 qingshu.
 * This file is part of the example-ayaka project.
 *
 * This project is licensed under the GPL-3.0 License.
 * See the LICENSE file for details.
 */
class ScheduleTaskServiceImpl(
    private val bot: Bot,
    private val userId: Long,
    private val groupId: Long = 0,
    private val task: ThreadPoolTaskScheduler,
) : ScheduleTaskService, Function<Request, Boolean> {

    override fun apply(t: Request): Boolean {
        log.info("Task creating... $t")
        task.schedule({
            if (0L != groupId) {
                val msg = MsgUtils.builder()
                    .at(userId)
                    .text(t.msg)
                    .build()
                val resp = bot.sendGroupMsg(groupId, msg, false)
                log.info("Schedule task execute completed: ${resp.status}")
                return@schedule
            }
            val resp = bot.sendPrivateMsg(userId, t.msg, false)
            log.info("Schedule task execute completed: ${resp.status}")
        }, Instant.now().plusSeconds(t.seconds))
        log.info("Schedule task create completed")
        return true
    }

    companion object {
        private val log = LoggerFactory.getLogger(ScheduleTaskServiceImpl::class.java)
    }
}

@JsonClassDescription(
    "Request object for scheduling a task. " +
            "This object allows you to specify when and what message to send in a scheduled task. " +
            "The 'seconds' field defines the delay in seconds before the task is executed. " +
            "For example, '60' means the task will run after 60 seconds. " +
            "You can set different delay times using seconds for immediate or short-term scheduling. " +
            "Examples: '30' for a 30-second delay, '3600' for a 1-hour delay, or '86400' for a 24-hour delay. " +
            "To schedule a task for a specific time, you will need to calculate the delay in seconds from the current time to the target time. " +
            "For instance, if you want the task to run at 3 PM and it's currently 2 PM, you would set 'seconds' to 3600 (1 hour). " +
            "The 'msg' field specifies the message content that will be sent when the task is executed. " +
            "Ensure the 'msg' field is properly set with the content you wish to be sent. For example: 'Reminder: Your meeting starts in 10 minutes.'"
)
data class Request(val seconds: Long = 0, val msg: String = "")