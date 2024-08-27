package io.github.qingshu.ayaka.example.controller

import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.multipart.MultipartException

/**
 * Copyright (c) 2024 qingshu.
 * This file is part of the example-ayaka project.
 *
 * This project is licensed under the GPL-3.0 License.
 * See the LICENSE file for details.
 */
@ControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(MultipartException::class)
    fun handleMultipartException(ex: MultipartException): ResponseEntity<Map<String, Any>> {
        return ResponseEntity.badRequest().body(
            mapOf("error" to "Invalid multipart request")
        )
    }

    companion object {
        private val log = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)
    }
}