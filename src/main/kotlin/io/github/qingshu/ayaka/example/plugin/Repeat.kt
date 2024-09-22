package io.github.qingshu.ayaka.example.plugin

import cn.hutool.core.util.RandomUtil
import io.github.qingshu.ayaka.dto.event.message.GroupMessageEvent
import io.github.qingshu.ayaka.example.config.EAConfig
import io.github.qingshu.ayaka.plugin.BotPlugin
import meteordevelopment.orbit.EventHandler
import net.jodah.expiringmap.ExpirationPolicy
import net.jodah.expiringmap.ExpiringMap
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit

/**
 * This file is part of the Yuri-Kotlin project:
 * https://github.com/MisakaTAT/Yuri-Kotlin
 *
 * Original Copyright (c) 2024 MisakaTAT
 * Licensed under the APGL-3.0 License. You may obtain a copy of the License at:
 *
 *     https://github.com/MisakaTAT/Yuri-Kotlin/blob/main/LICENSE
 *
 * Modifications:
 * - Modified by qingshu on 2024
 * - ignore
 *
 * This file is licensed under the same APGL-3.0 License.
 */
@Component
class Repeat: BotPlugin {

    private val cfg = EAConfig.plugins.repeat

    private val expiringMap: ExpiringMap<Long, String> = ExpiringMap.builder()
        .variableExpiration()
        .expirationPolicy(ExpirationPolicy.CREATED)
        .expiration(cfg.waitTime.times(1000L), TimeUnit.MILLISECONDS)
        .build()

    private val lastMsgMap: HashMap<Long, String> = hashMapOf()
    private val lastUserMap: HashMap<Long, Long> = hashMapOf()
    private val msgCountMap: HashMap<Long, Int> = hashMapOf()

    @EventHandler
    fun handler(event: GroupMessageEvent){
        val bot = event.bot!!
        val msg = event.message ?: return
        val groupId = event.groupId!!
        val userId = event.userId!!

        val cache = expiringMap[groupId]
        if(cache != null && msg == cache) return

        if(lastUserMap.getOrDefault(groupId, 0) == userId) return

        var count = msgCountMap.getOrDefault(groupId, 0)
        val lastMsg = lastMsgMap.getOrDefault(groupId, "")

        if(lastMsg == msg) {
            lastMsgMap[groupId] = msg
            lastUserMap[groupId] = userId
            msgCountMap[groupId] = ++count
            if(count == RandomUtil.randomInt(cfg.thresholdValue)){
                bot.sendGroupMsg(groupId, msg)
                val waitTime = cfg.waitTime.times(1000L)
                expiringMap.put(groupId, msg, waitTime, TimeUnit.MILLISECONDS)
                msgCountMap[groupId] = 0
            }
            return
        }

        lastMsgMap[groupId] = msg
        msgCountMap[groupId] = 1
        lastUserMap[groupId] = userId
    }
}