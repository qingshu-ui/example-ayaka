package io.github.qingshu.ayaka.example.service

import io.github.qingshu.ayaka.example.entity.DouYinVideoEntity

/**
 * Copyright (c) 2024 qingshu.
 * This file is part of the example-ayaka project.
 *
 * This project is licensed under the GPL-3.0 License.
 * See the LICENSE file for details.
 */
interface DouYinVideoService {

    fun getRandomUnusedVideo(count: Int, tag: String = ""): List<DouYinVideoEntity>

    fun markVideosAsUsed(entities: List<DouYinVideoEntity>)

    fun updateVideoInfo(videoInfo: DouYinVideoEntity)

    fun count(): Long

    fun requiredUpdateInfo(count: Int): List<DouYinVideoEntity>

    fun allUnUpdatedCount(): Int

    fun findAllTags(): List<String>

    fun requiredUpdateAuthorCount(): Int

    fun requiredUpdateAuthor(count: Int): List<DouYinVideoEntity>
}