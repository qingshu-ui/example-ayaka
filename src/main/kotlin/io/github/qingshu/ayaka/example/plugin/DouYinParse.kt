package io.github.qingshu.ayaka.example.plugin

import io.github.qingshu.ayaka.annotation.MessageHandlerFilter
import io.github.qingshu.ayaka.dto.constant.AtEnum
import io.github.qingshu.ayaka.dto.event.message.AnyMessageEvent
import io.github.qingshu.ayaka.example.annotation.Slf4j
import io.github.qingshu.ayaka.example.dto.DouYinParseDTO
import io.github.qingshu.ayaka.example.utils.NetUtils
import io.github.qingshu.ayaka.example.utils.Regex
import io.github.qingshu.ayaka.example.utils.RegexUtils
import io.github.qingshu.ayaka.plugin.BotPlugin
import io.github.qingshu.ayaka.utils.MsgUtils
import io.github.qingshu.ayaka.utils.mapper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
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
class DouYinParse(
    private val coroutine: CoroutineScope,
) : BotPlugin {

    private fun request(msg: String) = runCatching<DouYinParseDTO.Detail> {
        val shortURL = RegexUtils.group("url", msg, Regex.DOU_YIN_SHORT_URL).trim()
        if (shortURL.isBlank()) throw Exception("DouYin URL cannot be blank")
        val videoId = NetUtils.get(shortURL).use { resp ->
            val id = RegexUtils.group("id", resp.request.url.toString(), Regex.DOU_YIN_REAL_URL_ID)
            if (id.isBlank()) throw Exception("DouYin ID cannot be blank")
            id
        }

        return NetUtils.get("http://117.72.12.207/api/douyin/web/fetch_one_video?aweme_id=$videoId").use { resp ->
            val jsonStr = resp.body?.string().orEmpty()
            val data = mapper.readTree(jsonStr)
            mapper.treeToValue(data["data"], DouYinParseDTO::class.java)
        }.detail
    }.getOrThrow()

    @EventHandler
    @MessageHandlerFilter(at = AtEnum.NEED)
    fun handler(event: AnyMessageEvent) {
        if (!RegexUtils.check(event.message, Regex.DOU_YIN_SHORT_URL)) return
        val bot = event.bot
        val userId = event.userId
        bot.sendMsg(
            event = event,
            msg = MsgUtils.builder().at(userId).text("好的，宝贝，请稍等").build(),
        )
        coroutine.launch {
            kotlin.runCatching {
                val data = request(event.message)
                val msg = MsgUtils.builder()
                    .video(data.video.play.urls[0], data.video.cover.urls[0])
                    .build()
                bot.sendMsg(event, msg, false)
            }.onFailure {
                val msg = MsgUtils.builder()
                    .at(userId)
                    .text("哦，失败了嘛，不要灰心: ${it.message}")
                    .build()
                bot.sendMsg(event, msg)
            }
        }
    }
}