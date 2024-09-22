package io.github.qingshu.ayaka.example.entity

import jakarta.persistence.*

/**
 * This file is part of the Yuri-Kotlin project:
 * https://github.com/MisakaTAT/Yuri-Kotlin
 *
 * Original Copyright (c) 2024 MisakaTAT
 * Licensed under the APGL-3.0 License. You may obtain a copy of the License at:
 *
 *     https://github.com/MisakaTAT/Yuri-Kotlin/blob/main/LICENSE
 *
 * Modifications:
 * - Modified by qingshu on 2024
 * - ignore
 *
 * This file is licensed under the same APGL-3.0 License.
 */
@Entity
@Table(name = "drift_bottle")
data class DriftBottleEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int,

    @Column(nullable = false)
    val groupId: Long,

    @Column(nullable = false)
    val groupName: String,

    @Column(nullable = false)
    val userId: Long,

    @Column(nullable = false)
    val userName: String,

    @Column(nullable = false, columnDefinition = "longtext")
    val content: String,

    @Column(nullable = false)
    var open: Boolean = false,

    @Column(nullable = false)
    var openUser: Long = 0L,

    @Column(nullable = false)
    var openGroup: Long = 0L,

    @Column(nullable = false)
    var openUserName: String = "Unknown",

    @Column(nullable = false)
    var openGroupName: String = "Unknown",
)
