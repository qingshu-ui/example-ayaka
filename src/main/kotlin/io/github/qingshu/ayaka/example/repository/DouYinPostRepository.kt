package io.github.qingshu.ayaka.example.repository

import io.github.qingshu.ayaka.example.entity.DouYinPostEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

/**
 * Copyright (c) 2024 qingshu.
 * This file is part of the example-ayaka project.
 *
 * This project is licensed under the APG-3.0 License.
 * See the LICENSE file for details.
 */
@Repository
interface DouYinPostRepository: JpaRepository<DouYinPostEntity, Int> {


    @Transactional
    override fun <S : DouYinPostEntity> save(entity: S): S
}