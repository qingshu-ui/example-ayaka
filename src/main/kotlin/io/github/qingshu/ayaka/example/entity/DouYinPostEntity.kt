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
@Table(name = "dy_post")
data class DouYinPostEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int,

    @Column(nullable = false)
    val videoId: String,

    @Column(nullable = false)
    val tags: String = "",

    @Column(nullable = false)
    val isDeleted: Boolean = false,

    @Column(nullable = false)
    val size: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", referencedColumnName = "id", nullable = true)
    val author: DouYinAuthorEntity? = null,
)