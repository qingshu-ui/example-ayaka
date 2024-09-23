package io.github.qingshu.ayaka.example.service.impl

import io.github.qingshu.ayaka.example.entity.DouYinVideoEntity
import io.github.qingshu.ayaka.example.repository.DouYinVideoRepository
import io.github.qingshu.ayaka.example.service.DouYinVideoService
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
    private val repository: DouYinVideoRepository
) : DouYinVideoService {
    override fun getRandomUnusedVideo(count: Int): List<DouYinVideoEntity> {
        val entities = repository.findRandomUnusedVideo(count)
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

    override fun count(): Long {
        return repository.count()
    }

    @Scheduled(cron = "0 0 0 * * ?")
    private fun updateVideoInfoTask() {
        repository.findByUsedTodayIsTrue().forEach {
            it.usedToday = false
            updateVideoInfo(it)
        }
    }
}