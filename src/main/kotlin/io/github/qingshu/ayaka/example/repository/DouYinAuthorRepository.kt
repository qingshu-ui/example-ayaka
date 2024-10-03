package io.github.qingshu.ayaka.example.repository

import io.github.qingshu.ayaka.example.entity.DouYinAuthorEntity
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

/**
 * Copyright (c) 2024 qingshu.
 * This file is part of the example-ayaka project.
 *
 * This project is licensed under the APG-3.0 License.
 * See the LICENSE file for details.
 */
// @formatter:off
@Repository
interface DouYinAuthorRepository : JpaRepository<DouYinAuthorEntity, Int> {

    fun findBySecUid(secUid: String): List<DouYinAuthorEntity>

    @Query(
        "select a from DouYinAuthorEntity a " +
        "where a not in (select p.author from DouYinPostEntity p where p.author is not null)"
    )
    fun findAuthorWithoutPosts(pageable: Pageable = PageRequest.of(0, 1)): List<DouYinAuthorEntity>

    @Transactional
    override fun <S : DouYinAuthorEntity> save(entity: S): S
}
// @formatter:on