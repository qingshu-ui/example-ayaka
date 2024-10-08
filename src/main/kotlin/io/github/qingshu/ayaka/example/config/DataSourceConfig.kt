package io.github.qingshu.ayaka.example.config

import io.github.qingshu.ayaka.example.annotation.Slf4j
import io.github.qingshu.ayaka.example.annotation.Slf4j.Companion.log
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.sql.DataSource

/**
 * Copyright (c) 2024 qingshu.
 * This file is part of the example-ayaka project.
 *
 * This project is licensed under the AGPL-3.0 License.
 * See the LICENSE file for details.
 */
@Slf4j
@Configuration
class DataSourceConfig {

    @Bean
    fun getDataSource(): DataSource {
        val builder = DataSourceBuilder.create()

        builder.driverClassName("org.sqlite.JDBC")
        builder.url("jdbc:sqlite:ayaka.sqlite.db")
        log.info("使用 SQLite 数据库")
        return builder.build()
    }
}