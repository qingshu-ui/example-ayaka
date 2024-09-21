package io.github.qingshu.ayaka.example.repository

import io.github.qingshu.ayaka.example.entity.DriftBottleEntity
import jakarta.transaction.Transactional
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * Copyright (c) 2024 qingshu.
 * This file is part of the example-ayaka project.
 *
 * This project is licensed under the GPL-3.0 License.
 * See the LICENSE file for details.
 */
@Repository
interface DriftBottleRepository: JpaRepository<DriftBottleEntity, Int> {

    fun findAllByOpenIsFalseAndUserIdNotAndGroupIdNot(userId: Long, groupId: Long): List<DriftBottleEntity>

    fun countAllByOpenIsFalse(): Int

    @Transactional
    override fun <S : DriftBottleEntity> save(entity: S): S{
        TODO("Not yet implemented")
    }
}