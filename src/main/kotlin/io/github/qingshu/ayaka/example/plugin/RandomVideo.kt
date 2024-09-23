package io.github.qingshu.ayaka.example.plugin

import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import io.github.qingshu.ayaka.annotation.MessageHandlerFilter
import io.github.qingshu.ayaka.dto.event.message.AnyMessageEvent
import io.github.qingshu.ayaka.example.annotation.Slf4j
import io.github.qingshu.ayaka.example.annotation.Slf4j.Companion.log
import io.github.qingshu.ayaka.example.config.EAConfig
import io.github.qingshu.ayaka.example.entity.DouYinVideoEntity
import io.github.qingshu.ayaka.example.service.DouYinVideoService
import io.github.qingshu.ayaka.example.utils.NetUtils
import io.github.qingshu.ayaka.example.utils.Regex
import io.github.qingshu.ayaka.plugin.BotPlugin
import io.github.qingshu.ayaka.utils.MsgUtils
import io.github.qingshu.ayaka.utils.generateForwardMsg
import io.github.qingshu.ayaka.utils.mapper
import jakarta.annotation.PostConstruct
import kotlinx.coroutines.*
import meteordevelopment.orbit.EventHandler
import org.springframework.stereotype.Component
import java.io.File
import java.io.FileWriter
import java.io.PrintWriter
import java.security.MessageDigest
import java.util.*
import kotlin.io.path.Path
import kotlin.io.path.absolutePathString

/**
 * Copyright (c) 2024 qingshu.
 * This file is part of the example-ayaka project.
 *
 * This project is licensed under the GPL-3.0 License.
 * See the LICENSE file for details.
 */
@Slf4j
@Component
class RandomVideo(
    private val service: DouYinVideoService,
    private val coroutineScope: CoroutineScope,
) : BotPlugin {
    /**
     * 这个插件要做些什么？
     * 1 当收到指定的指令时
     * 将随机从指定的路径或者压缩包内提取对应数量（应该加以限制）的视频进行发送
     *
     * 2 如何做到随机性？
     * 3 如何存储视频信息？
     */

    private val config = EAConfig.plugins.randomVideo
    private val failedFileNames = Collections.synchronizedList(mutableListOf<String>())

    @PostConstruct
    fun initData() {
        if (0L != service.count()) return
        extractVideoInfoFromDirectory(config.path)
    }

    private fun extractVideoInfoFromDirectory(path: String) {
        val file = File(path)
        if (!file.exists() && !file.isDirectory) {
            log.error("$path is not a directory, database fill failed.")
            return
        }

        val jobs = mutableListOf<Job>()

        file.listFiles()?.filter { it.isFile && isVideoFile(it) }?.forEach { videoFile ->
            val job = coroutineScope.launch {
                try {
                    val fileName = videoFile.name
                    val fileSize = videoFile.length()
                    val md5 = calculateMD5(videoFile)
                    val (tags, desc) = getTags(videoFile.name)

                    val videoEntity = DouYinVideoEntity(
                        id = 0,
                        fileName = fileName,
                        md5 = md5,
                        size = fileSize,
                        description = desc,
                        tags = tags,
                    )
                    service.updateVideoInfo(videoEntity)
                } catch (e: Exception) {
                    log.error(e.message, e)
                    failedFileNames.add(videoFile.name)
                }
            }
            jobs.add(job)
        }

        coroutineScope.launch {
            while (jobs.any { it.isActive }) {
                delay(5000)
                flushFailedFileNames()
            }
        }

        runBlocking {
            jobs.forEach { it.join() }
        }
    }

    private fun getTags(fileName: String): List<String> {
        return try {
            val id = fileName.split("_")[0]
            val url = "http://117.72.12.207/api/douyin/web/fetch_one_video?aweme_id=$id"
            var jsonNode: ObjectNode
            NetUtils.get(url).use { resp ->
                jsonNode = mapper.readTree(resp.body?.string() ?: "") as ObjectNode
            }
            val videoTag = jsonNode["data"]["aweme_detail"]["video_tag"] as ArrayNode
            val desc = jsonNode["data"]["aweme_detail"]["desc"].asText()
            val tag = StringBuilder().apply {
                videoTag.forEachIndexed { idx, extra ->
                    append(extra["tag_name"].asText())
                    if (idx < videoTag.size() - 1) {
                        append(",")
                    }
                }
            }.toString()
            listOf(tag, desc)
        } catch (_: Exception) {
            log.error("$fileName 获取 tags 失败")
            listOf("", "")
        }
    }

    private fun calculateMD5(file: File): String {
        val md = MessageDigest.getInstance("MD5")
        file.inputStream().use { fis ->
            val buffer = ByteArray(1024)
            var bytesRead = fis.read(buffer)
            while (bytesRead != -1) {
                md.update(buffer, 0, bytesRead)
                bytesRead = fis.read(buffer)
            }
        }
        return md.digest().joinToString("") { "%02x".format(it) }
    }

    private fun flushFailedFileNames() {
        val logFile = File("logs/failed_files.log")
        logFile.parentFile?.mkdirs()

        if (failedFileNames.isNotEmpty()) {
            PrintWriter(FileWriter(logFile, true)).use { writer ->
                failedFileNames.forEach(writer::println)
            }
            failedFileNames.clear()
        }
    }

    private fun isVideoFile(file: File): Boolean {
        val videoExtension = setOf("mp4", "mkv", "avi", "mov", "flv")
        return videoExtension.any { file.name.endsWith(".$it", ignoreCase = true) }
    }

    @EventHandler
    @MessageHandlerFilter(cmd = Regex.RANDOM_VIDEO)
    fun handler(e: AnyMessageEvent) {
        val matcher = e.matcher ?: return
        val count = matcher.group(2)?.trim()?.toIntOrNull()?.coerceIn(1, 9) ?: 1
        val bot = e.bot

        val result = service.getRandomUnusedVideo(count)
        val msgBuilder = MsgUtils.builder()
        if (result.isEmpty()) {
            bot.sendMsg(e, msgBuilder.at(e.userId).text("已经被榨干了，没有了").build())
            return
        }
        if (result.size == 1) {
            bot.sendMsg(
                e,
                msgBuilder
                    .video("file://${Path(config.path, result[0].fileName).absolutePathString()}", "")
                    .build()
            )
            return
        }
        val msgList = mutableListOf<String>()
        result.forEach {
            val builder = MsgUtils.builder()
            msgList.add(builder.video("file://${Path(config.path, it.fileName).absolutePathString()}", "").build())
        }

        val forwardMsg = generateForwardMsg(123, "bot", msgList)
        coroutineScope.launch {
            bot.sendForwardMsg(e, forwardMsg)
        }
    }
}