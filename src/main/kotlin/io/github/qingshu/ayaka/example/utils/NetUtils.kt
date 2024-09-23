package io.github.qingshu.ayaka.example.utils

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.io.File
import java.util.concurrent.TimeUnit

/**
 * Copyright (c) 2024 qingshu.
 * This file is part of the example-ayaka project.
 *
 * This project is licensed under the AGPL-3.0 License.
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

    fun download(url: String, path: String, name: String, timeout: Int): String {
        File(path).let { if (!it.exists()) it.mkdirs() }
        val req = Request.Builder().url(url).build()
        val resp = client.newBuilder().readTimeout(timeout.toLong(), TimeUnit.SECONDS).build().newCall(req).execute()
        resp.code.let { if (it != 200) throw Exception("Can't download $name") }
        val inputStream = resp.body?.byteStream()
        val file = File(path, name)
        inputStream?.use { input ->
            file.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        return file.absolutePath
    }
}