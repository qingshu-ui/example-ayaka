package io.github.qingshu.ayaka.example.repository

import io.github.qingshu.ayaka.example.entity.DriftBottleEntity
import jakarta.transaction.Transactional
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * Copyright (c) 2024 https://github.com/MisakaTAT/Yuri-Kotlin.
 * This file is part of the https://github.com/MisakaTAT/Yuri-Kotlin project.
 *
 * This file is licensed under the AGPL-3.0 License.
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