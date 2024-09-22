package io.github.qingshu.ayaka.example.utils

import io.github.qingshu.ayaka.bot.Bot
import io.github.qingshu.ayaka.utils.MsgUtils

object SendUtils {

    fun group(groupId: Long, bot: Bot, text: String): Int? {
        return bot.sendGroupMsg(
            groupId,
            MsgUtils.builder().text(text).build(),
        ).data?.messageId
    }

    fun at(userId: Long, groupId: Long, bot: Bot, text: String): Int? {
        if (groupId != 0L) return bot.sendGroupMsg(
            groupId,
            MsgUtils.builder().at(userId).text(text).build(),
        ).data?.messageId
        return bot.sendPrivateMsg(userId, text).data?.messageId
    }
}