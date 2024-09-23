package io.github.qingshu.ayaka.example.entity

import jakarta.persistence.*

/**
 * Copyright (c) 2024 qingshu.
 * This file is part of the example-ayaka project.
 *
 * This project is licensed under the GPL-3.0 License.
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

    @Column(nullable = false)
    var description: String = "",

    @Column(nullable = false)
    var tags: String = "",

    @Column(nullable = false)
    var usedToday: Boolean = false
)