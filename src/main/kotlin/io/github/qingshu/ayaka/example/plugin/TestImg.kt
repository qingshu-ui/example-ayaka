package io.github.qingshu.ayaka.example.plugin

import io.github.qingshu.ayaka.dto.event.message.AnyMessageEvent
import io.github.qingshu.ayaka.example.annotation.Slf4j
import io.github.qingshu.ayaka.example.annotation.Slf4j.Companion.log
import io.github.qingshu.ayaka.plugin.BotPlugin
import io.github.qingshu.ayaka.utils.MsgUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import meteordevelopment.orbit.EventHandler
import org.springframework.stereotype.Component
import java.io.ByteArrayOutputStream
import java.net.URI
import java.util.*
import javax.imageio.ImageIO

/**
 * Copyright (c) 2024 qingshu.
 * This file is part of the example-ayaka project.
 *
 * This project is licensed under the GPL-3.0 License.
 * See the LICENSE file for details.
 */
@Slf4j
@Component
class TestImg(
    private val coroutineScope: CoroutineScope,
) : BotPlugin {

    @EventHandler
    fun handler(e: AnyMessageEvent) {
        val rawMsg = e.rawMessage ?: ""
        if(rawMsg.isEmpty() || rawMsg != "test") return
        val imgUrls = arrayOf(
            "https://i.pixiv.re/img-original/img/2022/11/29/21/02/54/103201474_p0.jpg",
            "https://i.pixiv.re/img-original/img/2023/05/13/00/10/06/108069486_p0.png",
            "https://i.pixiv.re/img-original/img/2022/03/29/20/43/59/97262943_p0.jpg",
        )

        val msgBuilder = MsgUtils.builder()

        coroutineScope.launch {
            val bot = e.bot!!

            imgUrls.forEach {
                withContext(Dispatchers.IO) {
                    val b64Img = imgUrl2B64(it)
                    msgBuilder.img("base64://$b64Img")
                }
            }
            val echo = bot.sendMsg(e, msgBuilder.build())
            log.info("{}", echo)
        }

    }

    private fun imgUrl2B64(url: String): String {
        val imgUrl = URI(url).toURL()
        val image = ImageIO.read(imgUrl)

        val outputStream = ByteArrayOutputStream()
        ImageIO.write(image, "png", outputStream)

        val imageBytes = outputStream.toByteArray()
        return Base64.getEncoder().encodeToString(imageBytes)
    }
}