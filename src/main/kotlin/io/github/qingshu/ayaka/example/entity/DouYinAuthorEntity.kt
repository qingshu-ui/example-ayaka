package io.github.qingshu.ayaka.example.entity

import jakarta.persistence.*

/**
 * Copyright (c) 2024 qingshu.
 * This file is part of the example-ayaka project.
 *
 * This project is licensed under the AGPL-3.0 License.
 * See the LICENSE file for details.
 */
@Entity
@Table(name = "dy_author")
data class DouYinAuthorEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int,

    @Column(nullable = false)
    val uid: String,

    @Column(nullable = false)
    val nickname: String,

    @Column(nullable = false)
    val secUid: String,
)
