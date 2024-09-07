package io.github.qingshu.ayaka.example.utils

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response

/**
 * Copyright (c) 2024 qingshu.
 * This file is part of the example-ayaka project.
 *
 * This project is licensed under the GPL-3.0 License.
 * See the LICENSE file for details.
 */
object NetUtils {

    private val mediaType = "application/json; charset=utf-8".toMediaType()

    private val client = OkHttpClient()

    fun get(url: String): Response {
        val req = Request.Builder().url(url).get().build()
        return client.newCall(req).execute()
    }

    fun get(url: String, headers: Map<String, String>): Response {
        val req = Request.Builder().url(url).get()
        headers.forEach {
            req.header(it.key, it.value)
        }
        return client.newCall(req.build()).execute()
    }

    fun post(url: String, json: String): Response {
        val req = Request.Builder().url(url).post(json.toRequestBody(mediaType))
        return client.newCall(req.build()).execute()
    }
}