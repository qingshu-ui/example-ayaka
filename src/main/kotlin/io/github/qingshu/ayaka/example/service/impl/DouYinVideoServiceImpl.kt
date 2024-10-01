package io.github.qingshu.ayaka.example.service.impl

import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import io.github.qingshu.ayaka.bot.BotFactory
import io.github.qingshu.ayaka.bot.BotSessionFactory
import io.github.qingshu.ayaka.example.annotation.Slf4j.Companion.log
import io.github.qingshu.ayaka.example.config.EAConfig
import io.github.qingshu.ayaka.example.entity.DouYinAuthorEntity
import io.github.qingshu.ayaka.example.entity.DouYinVideoEntity
import io.github.qingshu.ayaka.example.repository.DouYinAuthorRepository
import io.github.qingshu.ayaka.example.repository.DouYinVideoRepository
import io.github.qingshu.ayaka.example.service.DouYinVideoService
import io.github.qingshu.ayaka.example.utils.NetUtils
import io.github.qingshu.ayaka.utils.MsgUtils
import io.github.qingshu.ayaka.utils.mapper
import org.springframework.data.domain.PageRequest
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import kotlin.random.Random

/**
 * Copyright (c) 2024 qingshu.
 * This file is part of the example-ayaka project.
 *
 * This project is licensed under the GPL-3.0 License.
 * See the LICENSE file for details.
 */
@Service
class DouYinVideoServiceImpl(
    private val repository: DouYinVideoRepository,
    private val botFactory: BotFactory,
    private val sessionFactory: BotSessionFactory,
    private val authorRepository: DouYinAuthorRepository,
) : DouYinVideoService {
    private val bot = botFactory.createBot(EAConfig.base.selfId, sessionFactory.createSession("localhost"))

    override fun getRandomUnusedVideo(count: Int, tag: String): List<DouYinVideoEntity> {
        val entities = when {
            tag.isNotBlank() -> repository.findRandomUnusedVideo(count, tag)
            else -> repository.findRandomUnusedVideoWithoutTag(count)
        }
        markVideosAsUsed(entities)
        return entities
    }

    override fun markVideosAsUsed(entities: List<DouYinVideoEntity>) {
        entities.forEach {
            it.usedToday = true
            updateVideoInfo(it)
        }
    }

    override fun updateVideoInfo(videoInfo: DouYinVideoEntity) {
        repository.save(videoInfo)
    }

    override fun count(): Long = repository.count()

    override fun requiredUpdateInfo(count: Int): List<DouYinVideoEntity> {
        val pageable = PageRequest.of(0, count)
        return repository.findByUpdateStatus("pending", pageable = pageable)
    }

    override fun allUnUpdatedCount(): Int = repository.countByUpdateStatus()

    override fun findAllTags(): List<String> {
        return repository.findAllTags().asSequence()
            .flatMap { it.split(",").asSequence() }
            .map { it.trim() }.filter { it.isNotBlank() }
            .toSet()
            .toList()
    }

    override fun requiredUpdateAuthorCount() = repository.countByUpdateStatusAndAuthor()

    override fun requiredUpdateAuthor(count: Int): List<DouYinVideoEntity> {
        val pageable = PageRequest.of(0, count)
        return repository.findByUpdateStatusAndAuthor(pageable = pageable)
    }

    @Scheduled(cron = "0 0 0 * * ?")
    private fun resetUsedTodayStatus() {
        val allUsedToday = repository.findByUsedTodayIsTrue()
        EAConfig.base.adminList.forEach { admin ->
            bot.sendPrivateMsg(admin, "正在重置数据库，昨日使用 ${allUsedToday.size} 个视频")
        }
        allUsedToday.forEach {
            it.usedToday = false
            updateVideoInfo(it)
        }
    }

    @Scheduled(cron = "0 0/15 * * * ?")
    private fun updateTagsAndDesc() {
        if (allUnUpdatedCount() == 0) return
        log.info("开始更新视频信息")
        val requiredUpdateVideos = requiredUpdateInfo(Random.nextInt(50, 100))
        val startTipsMsg = MsgUtils.builder()
            .text("开始更新视频信息\n\n")
            .text("本次预计更新 ${requiredUpdateVideos.size} 条数据").build()
        EAConfig.base.adminList.forEach {
            bot.sendPrivateMsg(it, startTipsMsg)
        }
        val finished = requiredUpdateVideos.mapNotNull { video ->
            val result = getTags(video.fileName)
            video.tags = result["tags"].orEmpty()
            video.description = result["desc"].orEmpty()
            video.updateStatus = result["status"]!!
            video.failureReason = result["reason"].orEmpty()
            updateVideoInfo(video)
            if (video.updateStatus == "success") video else null
        }
        val endTipsMsg = MsgUtils.builder()
            .text("视频信息更新结束\n\n")
            .text("本次成功更新 ${finished.count()} 条数据\n")
            .text("剩余 ${allUnUpdatedCount()} 需要更新").build()
        EAConfig.base.adminList.forEach {
            bot.sendPrivateMsg(it, endTipsMsg)
        }
        log.info("视频信息更新结束")
    }

    @Scheduled(cron = "0 0/15 * * * ?")
    private fun updateAuthorInfo() {
        if (requiredUpdateAuthorCount() == 0) return
        log.info("开始更新 author 信息")
        val requiredUpdateVideos = requiredUpdateAuthor(Random.nextInt(50, 100))
        val startTipsMsg = MsgUtils.builder()
            .text("开始更新视频 author 信息\n\n")
            .text("本次预计更新 ${requiredUpdateVideos.size} 条数据").build()
        EAConfig.base.adminList.forEach {
            bot.sendPrivateMsg(it, startTipsMsg)
        }
        val finished = requiredUpdateVideos.mapNotNull { video ->
            val videoId = video.fileName.split("_")[0]
            val author = getAuthor(videoId)
            if (author != null) {
                video.author = author
                updateVideoInfo(video)
                return@mapNotNull video
            }
            null
        }
        val endTipsMsg = MsgUtils.builder()
            .text("视频 author 信息更新结束\n\n")
            .text("本次成功更新 ${finished.count()} 条数据\n")
            .text("剩余 ${allUnUpdatedCount()} 需要更新").build()
        EAConfig.base.adminList.forEach {
            bot.sendPrivateMsg(it, endTipsMsg)
        }
        log.info("视频 author 信息更新结束")
    }

    private fun getTags(fileName: String): Map<String, String> = runCatching<Map<String, String>> {
        val result = mutableMapOf(
            "status" to "pending",
            "tags" to "",
            "desc" to "",
            "reason" to "",
        )
        val id = fileName.split("_")[0]
        val url = "http://localhost/api/douyin/web/fetch_one_video?aweme_id=$id"
        val jsonNode = NetUtils.get(url).use { resp ->
            mapper.readTree(resp.body?.string().orEmpty()) as ObjectNode
        }
        val data = jsonNode["data"]
        if (data["aweme_detail"].isNull) {
            result["status"] = "failed"
            result["reason"] = data["filter_detail"]["detail_msg"].asText().orEmpty()
            log.warn("${result["status"]}: ${result["reason"]}")
            return@runCatching result
        }
        val videoTag = data["aweme_detail"]["video_tag"] as ArrayNode
        result["tags"] = videoTag.mapNotNull { it["tag_name"].asText().takeIf { text -> text.isNotBlank() } }
            .joinToString(",")
        result["desc"] = data["aweme_detail"]["desc"].asText().orEmpty()
        result["status"] = "success"
        result
    }.getOrDefault(
        mapOf(
            "status" to "failed",
            "reason" to "",
            "tags" to "",
            "desc" to "",
        )
    )

    private fun getAuthor(videoId: String): DouYinAuthorEntity? {
        return try {
            val url = "http://localhost/api/douyin/web/fetch_one_video?aweme_id=$videoId"
            val jsonNode = NetUtils.get(url).use { resp ->
                mapper.readTree(resp.body?.string().orEmpty()) as ObjectNode
            }
            val data = jsonNode["data"]
            if (data["aweme_detail"].isNull) {
                return null
            }
            val authorJson = data["aweme_detail"]["author"] as ObjectNode
            val uid = authorJson["uid"].asText()
            val nickname = authorJson["nickname"].asText()
            val secUid = authorJson["sec_uid"].asText()
            val authorList = authorRepository.findBySecUid(secUid)
            if (authorList.isNotEmpty()) return authorList[0]
            val author = DouYinAuthorEntity(id = 0, uid = uid, nickname = nickname, secUid = secUid)
            authorRepository.save(author)
        } catch (e: Exception) {
            log.error("获取 author 失败")
            null
        }
    }
}