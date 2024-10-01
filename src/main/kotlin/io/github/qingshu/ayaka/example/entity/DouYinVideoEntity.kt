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
@Table(name = "video_info")
data class DouYinVideoEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int,

    @Column(nullable = false)
    val fileName: String,

    @Column(nullable = false)
    val md5: String,

    @Column(nullable = false)
    val size: Long,

    @Column(nullable = false, columnDefinition = "longtext")
    var description: String = "",

    @Column(nullable = false)
    var tags: String = "",

    @Column(nullable = false)
    var usedToday: Boolean = false,

    /**
     * pending, success, failed
     */
    @Column(nullable = false)
    var updateStatus: String = "pending",

    @Column(nullable = false)
    var failureReason: String = "",

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = true, referencedColumnName = "id")
    var author: DouYinAuthorEntity? = null,
)