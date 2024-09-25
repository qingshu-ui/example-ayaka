package io.github.qingshu.ayaka.example.service.impl

import io.github.qingshu.ayaka.bot.BotFactory
import io.github.qingshu.ayaka.bot.BotSessionFactory
import io.github.qingshu.ayaka.example.config.EAConfig
import io.github.qingshu.ayaka.example.entity.DouYinVideoEntity
import io.github.qingshu.ayaka.example.repository.DouYinVideoRepository
import io.github.qingshu.ayaka.example.service.DouYinVideoService
import org.springframework.data.domain.PageRequest
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

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
) : DouYinVideoService {
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

    @Scheduled(cron = "0 0 0 * * ?")
    private fun updateVideoInfoTask() {
        val baseConfig = EAConfig.base
        val botSession = sessionFactory.createSession("localhost")
        val bot = botFactory.createBot(baseConfig.selfId, botSession)
        val allUsedToday = repository.findByUsedTodayIsTrue()
        baseConfig.adminList.forEach { admin ->
            bot.sendPrivateMsg(admin, "正在重置数据库，昨日使用 ${allUsedToday.size} 个视频")
        }
        allUsedToday.forEach {
            it.usedToday = false
            updateVideoInfo(it)
        }
    }

    override fun requiredUpdateInfo(count: Int): List<DouYinVideoEntity> {
        val pageable = PageRequest.of(0, count)
        return repository.findByTagsAndDescription(pageable = pageable)
    }

    override fun allUnUpdatedCount(): Int = repository.countByTagsAndDescription()

    override fun findAllTags(): List<String> {
        return repository.findAllTags().asSequence()
            .flatMap { it.split(",").asSequence() }
            .map { it.trim() }.filter { it.isNotBlank() }
            .toSet()
            .toList()
    }
}