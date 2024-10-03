package io.github.qingshu.ayaka.example.plugin

import com.fasterxml.jackson.databind.node.ArrayNode
import io.github.qingshu.ayaka.annotation.MessageHandlerFilter
import io.github.qingshu.ayaka.bot.BotFactory
import io.github.qingshu.ayaka.bot.BotSessionFactory
import io.github.qingshu.ayaka.dto.event.message.GroupMessageEvent
import io.github.qingshu.ayaka.dto.event.message.PrivateMessageEvent
import io.github.qingshu.ayaka.example.annotation.Slf4j
import io.github.qingshu.ayaka.example.annotation.Slf4j.Companion.log
import io.github.qingshu.ayaka.example.config.EAConfig
import io.github.qingshu.ayaka.example.entity.DouYinPostEntity
import io.github.qingshu.ayaka.example.repository.DouYinAuthorRepository
import io.github.qingshu.ayaka.example.repository.DouYinPostRepository
import io.github.qingshu.ayaka.example.service.DouYinPostService
import io.github.qingshu.ayaka.plugin.BotPlugin
import io.github.qingshu.ayaka.utils.MsgUtils
import meteordevelopment.orbit.EventHandler
import org.springframework.scheduling.annotation.Scheduled
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
class ScheduleTask(
    private val sessionFactory: BotSessionFactory,
    private val botFactory: BotFactory,
    private val douService: DouYinPostService,
    private val postRepository: DouYinPostRepository,
    private val authorRepository: DouYinAuthorRepository,
): BotPlugin {

    private val bot = botFactory.createBot(EAConfig.base.selfId, sessionFactory.createSession("localhost"))

    @EventHandler
    @MessageHandlerFilter(cmd = "like")
    fun handler(event: PrivateMessageEvent) {
        val bot = event.bot
        val userId = event.userId
        bot.sendPrivateMsg(
            userId = userId,
            msg = MsgUtils.builder().reply(event.messageId).text("欧克呀，宝").build()
        )
        val result = bot.sendLike(userId)
        log.info("Action like done: {}", result)
    }

    @EventHandler
    @MessageHandlerFilter(cmd = "sign")
    fun handler(event: GroupMessageEvent) {
        val bot = event.bot
        val echo = bot.sendGroupSign(event.groupId)
        log.info("Action sign done: {}", echo)
    }

    @Scheduled(cron = "5 0 0 * * ?")
    fun friendLike() {
        val userId = 1718692748L
        val botSession = sessionFactory.createSession("localhost")
        val bot = botFactory.createBot(EAConfig.base.selfId, botSession)
        val rel = bot.sendLike(userId)
        bot.sendPrivateMsg(userId, "点赞：${rel.status}")
    }

    @Scheduled(cron = "0 0/15 * * * ?")
    // @EventHandler
    // @MessageHandlerFilter(cmd = "test")
    fun firstPullPost(/*e: PrivateMessageEvent*/) {
        val authorList = authorRepository.findAuthorWithoutPosts()
        if (authorList.isEmpty()) return
        val author = authorList.first()
        EAConfig.base.adminList.forEach { adminId ->
            bot.sendPrivateMsg(adminId, MsgUtils().text("正在获取 [${author.nickname}] 的作品数据").build())
        }
        val allVideo = fetchAllUserPostVideos(author.secUid, 0)
        val finished = allVideo.mapNotNull { video ->
            video.author = author
            kotlin.runCatching {
                postRepository.save(video)
            }.getOrElse {
                log.error(it.message)
                null
            }
        }
        EAConfig.base.adminList.forEach { adminId ->
            bot.sendPrivateMsg(
                adminId,
                MsgUtils().text("[${author.nickname}] 成功获取 ${finished.count()} 个作品数据").build()
            )
        }
    }

    fun fetchAllUserPostVideos(secUid: String, cursor: Long): List<DouYinPostEntity> {
        val allVideos = mutableListOf<DouYinPostEntity>()
        val result = douService.fetchUserPostVideos(secUid, cursor).let {
            if (!it.has("data")) {
                log.error("Response does not contain 'data': $it")
                return allVideos
            }
            it["data"]
        }

        val hasMore = result["has_more"]?.asInt() == 1
        val maxCursor = result["max_cursor"]?.asLong()
        val videoList = result["aweme_list"] as? ArrayNode

        videoList?.forEach { videoJson ->
            val videoId = videoJson["aweme_id"]?.asText()
            val tags = (videoJson["video_tag"] as? ArrayNode)
                ?.mapNotNull { it["tag_name"]?.asText() }
                ?.filter { it.isNotBlank() }
                ?.joinToString(",") ?: ""
            if (videoId != null) {
                allVideos.add(DouYinPostEntity(0, videoId, tags = tags))
            }
        }

        if (hasMore && maxCursor != null) {
            allVideos.addAll(fetchAllUserPostVideos(secUid, maxCursor))
        }
        return allVideos
    }
}