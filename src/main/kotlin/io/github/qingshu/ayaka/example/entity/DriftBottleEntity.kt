package io.github.qingshu.ayaka.example.entity

import jakarta.persistence.*

/**
 * Copyright (c) 2024 https://github.com/MisakaTAT/Yuri-Kotlin.
 * This file is part of the https://github.com/MisakaTAT/Yuri-Kotlin project.
 *
 * This file is licensed under the AGPL-3.0 License.
 * See the LICENSE file for details.
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
