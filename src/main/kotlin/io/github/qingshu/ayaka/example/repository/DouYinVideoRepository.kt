package io.github.qingshu.ayaka.example.repository

import io.github.qingshu.ayaka.example.entity.DouYinVideoEntity
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

/**
 * Copyright (c) 2024 qingshu.
 * This file is part of the example-ayaka project.
 *
 * This project is licensed under the GPL-3.0 License.
 * See the LICENSE file for details.
 */
@Repository
interface DouYinVideoRepository : JpaRepository<DouYinVideoEntity, Int> {

    fun existsByMd5(md5: String): Boolean

    @Query(
        value = "select * from video_info where used_today=false order by random() limit :count",
        nativeQuery = true
    )
    fun findRandomUnusedVideo(@Param("count") count: Int): List<DouYinVideoEntity>

    fun findByUsedTodayIsTrue(): List<DouYinVideoEntity>

    fun findByTagsAndDescription(tags: String = "", descriptions: String = "", pageable: Pageable): List<DouYinVideoEntity>

    @Transactional
    override fun <S : DouYinVideoEntity> save(entity: S): S {
        TODO("Not yet implemented")
    }
}