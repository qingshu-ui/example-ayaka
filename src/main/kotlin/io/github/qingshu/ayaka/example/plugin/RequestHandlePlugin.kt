package io.github.qingshu.ayaka.example.plugin

import io.github.qingshu.ayaka.dto.event.message.PrivateMessageEvent
import io.github.qingshu.ayaka.dto.event.request.FriendAddRequestEvent
import io.github.qingshu.ayaka.dto.event.request.GroupAddRequestEvent
import io.github.qingshu.ayaka.plugin.BotPlugin
import meteordevelopment.orbit.EventHandler
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

/**
 * Copyright (c) 2024 qingshu.
 * This file is part of the example-ayaka project.
 *
 * This project is licensed under the GPL-3.0 License.
 * See the LICENSE file for details.
 */
@Component
class RequestHandlePlugin : BotPlugin {

    @EventHandler
    fun onFriendRequest(event: FriendAddRequestEvent) {
        val bot = event.bot!!
        val flag = event.flag
        if (null != flag) {
            val rel = bot.setFriendAddRequest(flag, true, "")
            rel.run {
                log.info("Process friend add: ${this.status}")
            }
        }
    }

    @EventHandler
    fun onPrivateMessage(event: PrivateMessageEvent) {
        val bot = event.bot!!
        val msg = event.rawMessage
        if ("get" == msg) {
            val rel = bot.getGroupList()
            log.info("get cmd: $rel")
        }
        if ("sign" == msg) {
            val groups = bot.getGroupList()
            groups.data?.forEach {
                val rel = bot.sendGroupSign(it.groupId!!)
                log.info("sign ${rel.status}")
            }
        }
    }

    @EventHandler
    fun onGroupAddRequest(event: GroupAddRequestEvent) {
        val bot = event.bot!!
        val flag = event.flag!!
        val subType = event.subType!!
        val rel = bot.setGroupAddRequest(flag, subType, true, "")
        rel.run {
            log.info("Process group add: ${this.status}")
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(RequestHandlePlugin::class.java)
    }
}