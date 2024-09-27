package io.github.qingshu.ayaka.example.plugin

import io.github.qingshu.ayaka.annotation.MessageHandlerFilter
import io.github.qingshu.ayaka.dto.constant.AtEnum
import io.github.qingshu.ayaka.dto.event.message.GroupMessageEvent
import io.github.qingshu.ayaka.dto.event.message.MessageEvent
import io.github.qingshu.ayaka.example.annotation.Slf4j
import io.github.qingshu.ayaka.example.annotation.Slf4j.Companion.log
import io.github.qingshu.ayaka.example.config.EAConfig
import io.github.qingshu.ayaka.example.service.PermissionManager
import io.github.qingshu.ayaka.example.utils.Regex
import io.github.qingshu.ayaka.plugin.BotPlugin
import io.github.qingshu.ayaka.utils.MsgUtils
import io.github.qingshu.ayaka.utils.getAtList
import meteordevelopment.orbit.EventHandler
import org.springframework.stereotype.Component

/**
 * Copyright (c) 2024 qingshu.
 * This file is part of the example-ayaka project.
 *
 * This project is licensed under the AGPL-3.0 License.
 * See the LICENSE file for details.
 */
@Slf4j
@Component
class AdminCommand(
    private val permissionManager: PermissionManager,
) : BotPlugin {

    private val cfg get() = EAConfig.base

    fun noPermissionHandler(event: MessageEvent) {
        val bot = event.bot
        val userId = event.userId
        val msg = MsgUtils.builder().at(userId).text("你没有权限使用这个指令嘎").build()
        log.info(msg)
        bot.sendMsg(event, msg)
    }

    private fun registerCmd(cmd: String) {
        if (permissionManager.isRegistered(cmd)) return
        permissionManager.register(cmd) {
            denyHandler = ::noPermissionHandler
            checker = { it in cfg.adminList }
        }
    }

    private fun executeCommand(cmd: String, event: MessageEvent, handler: (MessageEvent) -> Unit) {
        permissionManager.grantHandler(cmd, handler)
        permissionManager.execute(cmd, event)
    }

    init {
        val cmdList = listOf(
            "禁言", "退群"
        )
        cmdList.forEach(::registerCmd)
    }

    @EventHandler
    @MessageHandlerFilter(cmd = "退群", at = AtEnum.NEED)
    fun groupQuitCmd(event: GroupMessageEvent) {
        val cmd = event.rawMessage
        executeCommand(cmd, event) {
            val bot = event.bot
            val msg = MsgUtils.builder().reply(event.messageId).text("收到").build()
            bot.sendGroupMsg(event.groupId, msg)
            val echo = bot.setGroupLeave(event.groupId, false)
            log.info("Executed cmd: {}", echo)
        }
    }

    @EventHandler
    @MessageHandlerFilter(cmd = Regex.BAN_CMD)
    fun groupBanCmd(event: GroupMessageEvent) {
        val cmd = "禁言"
        val bot = event.bot
        val groupId = event.groupId
        val arrayMsg = event.arrayMsg
        val userList = getAtList(arrayMsg).distinct()
        val emptyUserMsg = MsgUtils.builder().reply(event.messageId)
            .text("你搁那禁言空气呢").build()
        if (userList.isEmpty()) {
            bot.sendGroupMsg(groupId, emptyUserMsg)
            return
        }
        executeCommand(cmd, event, ::groupBanHandler)
    }

    fun groupBanHandler(event: MessageEvent) {
        if (event !is GroupMessageEvent) return
        val userList = getAtList(event.arrayMsg).distinct()
        val bot = event.bot
        val groupId = event.groupId
        val matcher = event.matcher!!
        val duration = matcher.group(3)?.trim()?.toIntOrNull()?.coerceIn(1, 86400) ?: 60
        val banSelfMsg = MsgUtils.builder()
            .reply(event.messageId)
            .text("什么？你还要禁言我？你良心去哪了！！")
            .build()
        val finish = userList.mapNotNull { userId ->
            if (userId == bot.selfId) {
                bot.sendGroupMsg(groupId, banSelfMsg)
                return@mapNotNull null
            }
            val echo = bot.setGroupBan(groupId, userId, duration)
            log.info("{}", echo)
            if (echo.status != "ok") return@mapNotNull null
            userId
        }
        if (finish.isEmpty()) return
        val msg = MsgUtils.builder()
            .reply(event.messageId)
            .text("完成辽\n")
            .text("有以下倒霉蛋被禁言了\n\n")
        finish.forEachIndexed { idx, userId ->
            msg.at(userId = userId)
            if (idx != finish.size - 1) msg.text("\n")
        }
        bot.sendGroupMsg(groupId, msg.build())
    }

}