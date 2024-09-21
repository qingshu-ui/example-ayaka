package io.github.qingshu.ayaka.example.plugin

import com.alibaba.fastjson2.JSON
import io.github.qingshu.ayaka.annotation.MessageHandlerFilter
import io.github.qingshu.ayaka.dto.constant.AtEnum
import io.github.qingshu.ayaka.dto.event.message.AnyMessageEvent
import io.github.qingshu.ayaka.example.annotation.Slf4j
import io.github.qingshu.ayaka.example.annotation.Slf4j.Companion.log
import io.github.qingshu.ayaka.example.dto.DouYinParseDTO
import io.github.qingshu.ayaka.example.utils.NetUtils
import io.github.qingshu.ayaka.example.utils.Regex
import io.github.qingshu.ayaka.example.utils.RegexUtils
import io.github.qingshu.ayaka.plugin.BotPlugin
import io.github.qingshu.ayaka.utils.MsgUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import meteordevelopment.orbit.EventHandler
import org.springframework.stereotype.Component

/**
 * Copyright (c) 2024 qingshu.
 * This file is part of the example-ayaka project.
 *
 * This project is licensed under the GPL-3.0 License.
 * See the LICENSE file for details.
 */
@Slf4j
@Component
class DouYinParse(
    private val coroutine: CoroutineScope,
) : BotPlugin {

    private fun request(msg: String): DouYinParseDTO.Detail {
        val shortURL = RegexUtils.group("url", msg, Regex.DOU_YIN_SHORT_URL).trim()
        if (shortURL.isBlank()) throw Exception("DouYin URL cannot be blank")
        val videoId = NetUtils.get(shortURL).use { resp ->
            val id = RegexUtils.group("id", resp.request.url.toString(), Regex.DOU_YIN_REAL_URL_ID)
            if (id.isBlank()) throw Exception("DouYin ID cannot be blank")
            id
        }

        return NetUtils.get("http://117.72.12.207/api/douyin/web/fetch_one_video?aweme_id=$videoId").use { resp ->
            val data = JSON.parseObject(resp.body?.string())
            JSON.parseObject(data["data"].toString(), DouYinParseDTO::class.java) ?: throw Exception("DouYin parse failed")
        }.detail
    }

    @EventHandler
    @MessageHandlerFilter(at = AtEnum.NEED)
    fun handler(event: AnyMessageEvent) {
        try {
            if (!RegexUtils.check(event.message ?: "", Regex.DOU_YIN_SHORT_URL)) return
            val bot = event.bot!!
            val messageId = event.messageId!!
            bot.sendMsg(
                event = event,
                msg = MsgUtils.builder().reply(messageId).text("好的，宝子").build(),
            )
            coroutine.launch {
                val data = request(event.message!!)
                val msg = MsgUtils.builder()
                    .video(data.video.play.urls[0], data.video.cover.urls[0])
                    .build()
                val echo = bot.sendMsg(event, msg, false)
                log.info("$echo")
            }
        } catch (e: Exception) {
            throw e
        }
    }
}