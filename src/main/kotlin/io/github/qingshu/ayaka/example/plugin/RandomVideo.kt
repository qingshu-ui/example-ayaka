package io.github.qingshu.ayaka.example.plugin

import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import io.github.qingshu.ayaka.annotation.MessageHandlerFilter
import io.github.qingshu.ayaka.bot.BotFactory
import io.github.qingshu.ayaka.bot.BotSessionFactory
import io.github.qingshu.ayaka.dto.constant.AtEnum
import io.github.qingshu.ayaka.dto.event.message.AnyMessageEvent
import io.github.qingshu.ayaka.example.annotation.Slf4j
import io.github.qingshu.ayaka.example.annotation.Slf4j.Companion.log
import io.github.qingshu.ayaka.example.config.EAConfig
import io.github.qingshu.ayaka.example.entity.DouYinVideoEntity
import io.github.qingshu.ayaka.example.plugin.RandomMessages.emptyResultMessage
import io.github.qingshu.ayaka.example.plugin.RandomMessages.emptyResultMessageWithTag
import io.github.qingshu.ayaka.example.plugin.RandomMessages.rateLimitMessages
import io.github.qingshu.ayaka.example.plugin.RandomMessages.waitMessage
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
import net.jodah.expiringmap.ExpirationPolicy
import net.jodah.expiringmap.ExpiringMap
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.io.File
import java.io.FileWriter
import java.io.PrintWriter
import java.security.MessageDigest
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.io.path.Path
import kotlin.io.path.absolutePathString
import kotlin.random.Random

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
    private val botFactory: BotFactory,
    private val sessionFactory: BotSessionFactory
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
    private val expiringMap: ExpiringMap<Long, Long> = ExpiringMap.builder()
        .variableExpiration()
        .expirationPolicy(ExpirationPolicy.CREATED)
        .build()
    private val expiringGetTagMap: ExpiringMap<Long, Long> = ExpiringMap.builder()
        .variableExpiration()
        .expirationPolicy(ExpirationPolicy.CREATED)
        .build()

    @PostConstruct
    fun initData() {
        if (0L != service.count()) return
        extractVideoInfoFromDirectory(config.path)
    }

    @Scheduled(cron = "0 0/10 * * * ?")
    fun updateVideoInfo() {
        log.info("开始更新视频信息")
        val baseConfig = EAConfig.base
        val botSession = sessionFactory.createSession("localhost")
        val bot = botFactory.createBot(baseConfig.selfId, botSession)
        val requiredUpdateVideos = service.requiredUpdateInfo(50)
        val startTipsMsg = MsgUtils.builder()
            .text("开始更新视频信息\n\n")
            .text("本次预计更新 ${requiredUpdateVideos.size} 条数据").build()
        baseConfig.adminList.forEach {
            bot.sendPrivateMsg(it, startTipsMsg)
        }
        val finished = requiredUpdateVideos.mapNotNull { video ->
            val (tags, desc) = getTags(video.fileName)
            if (tags.isNotEmpty() || desc.isNotEmpty()) {
                video.apply {
                    this.tags = tags
                    this.description = desc
                }.also { updatedVideo ->
                    service.updateVideoInfo(updatedVideo)
                }
            } else null
        }
        val endTipsMsg = MsgUtils.builder()
            .text("视频信息更新结束\n\n")
            .text("本次成功更新 ${finished.count()} 条数据\n")
            .text("剩余 ${service.allUnUpdatedCount()} 需要更新").build()
        baseConfig.adminList.forEach {
            bot.sendPrivateMsg(it, endTipsMsg)
        }
        log.info("视频信息更新结束")
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
                    val (tags, desc) = "" to ""

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
            val url = "http://localhost/api/douyin/web/fetch_one_video?aweme_id=$id"
            val jsonNode = NetUtils.get(url).use { resp ->
                mapper.readTree(resp.body?.string().orEmpty()) as ObjectNode
            }

            val tags = kotlin.runCatching {
                val videoTag = jsonNode["data"]["aweme_detail"]["video_tag"] as ArrayNode
                videoTag.mapNotNull { it["tag_name"].asText().takeIf { text -> text.isNotBlank() } }
                    .joinToString(",")
            }.onFailure {
                log.error("$fileName 获取 tags 失败")
            }.getOrDefault("")

            val desc = kotlin.runCatching {
                jsonNode["data"]["aweme_detail"]["desc"].asText()
            }.onFailure {
                log.error("$fileName 获取 desc 失败")
            }.getOrDefault("")

            listOf(tags, desc)
        } catch (e: Exception) {
            log.error("$fileName ${e.message}")
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
        val tag = matcher.group(3)?.trim().orEmpty()
        val bot = e.bot
        val groupId = e.groupId
        val userId = e.userId

        val id = if (groupId != 0L) groupId else userId
        expiringMap[id]?.let {
            if (it == userId) {
                val expectedExpiration = expiringMap.getExpectedExpiration(id) / 1000

                val randomMessage =
                    rateLimitMessages[Random.nextInt(rateLimitMessages.size)]
                bot.sendMsg(
                    e, MsgUtils.builder()
                        .reply(e.messageId)
                        .text(randomMessage.replace("{}", "$expectedExpiration"))
                        .build()
                )
                return
            }
        }
        expiringMap.put(id, userId, config.cd.toLong(), TimeUnit.SECONDS)

        val result = service.getRandomUnusedVideo(count, tag)
        if (result.isEmpty()) {
            val message = when {
                tag.isBlank() -> emptyResultMessage[Random.nextInt(emptyResultMessage.size)]
                else -> emptyResultMessageWithTag[Random.nextInt(emptyResultMessageWithTag.size)].replace("{}", tag)
            }
            bot.sendMsg(e, MsgUtils.builder().at(e.userId).text(message).build())
            return
        }
        val videoMessages = result.map {
            MsgUtils.builder()
                .video("file://${Path(config.path, it.fileName).absolutePathString()}", "")
                .build()
        }
        if (videoMessages.size == 1) {
            bot.sendMsg(e, videoMessages.first())
            return
        }
        bot.sendMsg(
            e,
            MsgUtils.builder().reply(e.messageId).text(waitMessage[Random.nextInt(waitMessage.size)]).build()
        )
        val forwardMsg = generateForwardMsg(123, "bot", videoMessages)
        coroutineScope.launch {
            bot.sendForwardMsg(e, forwardMsg)
        }
    }

    @EventHandler
    @MessageHandlerFilter(cmd = Regex.GET_TAGS_CMD, at = AtEnum.NEED)
    fun getTagsCmd(e: AnyMessageEvent) {
        val bot = e.bot
        val expiringId = if (e.groupId == 0L) e.userId else e.groupId
        val cd = expiringGetTagMap[expiringId]
        if (cd != null && cd == e.userId) {
            return
        }
        expiringGetTagMap.put(expiringId, e.userId, 2 * 60, TimeUnit.SECONDS)

        val allTags = service.findAllTags()
        val messageBuilder = MsgUtils.builder()
            .reply(e.messageId)
            .text("官爷，这是咱这最好的了，请过目：\n")
            .build()
        val allTagsMsg = listOf(messageBuilder) + allTags.map {
            MsgUtils.builder().text("$it\n").build()
        }
        val forwardMsg = generateForwardMsg(123, "bot", allTagsMsg)
        bot.sendForwardMsg(e, forwardMsg)
    }
}

private object RandomMessages {

    val rateLimitMessages = listOf(
        "呜～ 还请不要太快，だめ··· 冷却：[{}秒]",
        "呜～ 太快了会坏掉的··· 冷却：[{}}秒]",
        "呜～不要太快哦...我还需要休息一下...冷却：[{}}秒]",
        "欸～你已经让我没力气了，再给我[{}秒]恢复下吧~",
        "啊～太激烈了...我还需要冷静一下~冷却中：[{}秒]",
        "你这么急的话，我也撑不住呀~再给我[{}秒]嘛！",
        "呀～冷却还没结束呢...要不我们稍微放慢一点？[{}秒]之后再来吧~",
        "呜～ 再等一等，恢复需要时间...冷却：[{}秒]",
        "哎呀，慢点会更好哦，给我[{}秒]调整一下！",
        "呀～ 你这么急，我有点受不了呢...请稍等[{}秒]！",
        "呜～ 冷却中，请稍等[{}秒]，我需要恢复力量！",
        "欸～ 不要太快，我还在喘气...再等我[{}秒]吧！",
        "啊～太激烈了...我还需要[{}秒]的时间来冷静！",
        "你这么着急，我也有点压力，给我[{}秒]放松一下！",
        "哎呀，真是太快了，等我[{}秒]再继续哦！",
        "呜～ 请不要催我，冷却需要[{}秒]的时间！",
        "呀～ 再给我[{}秒]，我还没准备好呢！",
        "你让我喘不过气了，等我[{}秒]再来吧！",
        "呜～ 还请稍等，我需要[{}秒]来恢复状态！",
        "呀～ 再给我[{}秒]的时间，我会更好的！",
        "哎呀，稍微慢一点好不好？我需要[{}秒]调整！",
        "啊～ 冷却还没有结束，请耐心等待[{}秒]！",
        "你这么急，我快撑不住了，再给我[{}秒]！",
        "呜～ 不要太快哦，我需要[{}秒]的恢复时间！",
        "呀～ 我还在缓冲中，等我[{}秒]再继续吧！",
        "哎呀，真是太激烈了，再给我[{}秒]放松一下！",
        "呜～ 请慢一点，我还需要[{}秒]来冷却！",
        "呀～ 不要急，给我[{}秒]时间来恢复吧！",
        "哎呀，我快跟不上了，再等我[{}秒]！",
        "呜～ 这样太快了，我需要[{}秒]来缓和！",
        "呀～ 冷却还在进行中，等我[{}秒]吧！",
        "呜～ 我还需要[{}秒]的时间来冷静！",
        "啊～ 请稍等，我需要[{}秒]来调整状态！",
        "哎呀，快点会让我很紧张，请等我[{}秒]！",
        "呜～ 再给我[{}秒]的冷却时间，我才能继续哦！",
        "你这么急，真的让我很紧张，等我[{}秒]！",
        "呀～ 我还没准备好，再等我[{}秒]再来吧！",
        "呜～ 请不要太快，我需要[{}秒]的恢复！",
        "啊～ 再给我[{}秒]，我会更好的！",
        "哎呀，太快了，给我点时间，等我[{}秒]！",
        "呜～ 这样的速度让我有点受不了，再等我[{}秒]！",
        "呀～ 我还需要[{}秒]的时间，稍微慢一点吧！",
        "哎呀，太急了，我需要冷却[{}秒]！",
        "呜～ 再等一等吧，我需要[{}秒]恢复！",
        "呀～ 不要太快，我还在调整，等我[{}秒]！",
        "哎呀，这样会让我很紧张，再等我[{}秒]！",
        "呜～ 冷却还没结束，等我[{}秒]再来！",
        "啊～ 我还需要[{}秒]，请稍微放慢！",
        "呜～ 再给我[{}秒]的时间，我会更好的！",
        "呀～ 冷却中，等我[{}秒]再来哦！",
        "哎呀，稍微慢一点，我需要[{}秒]来缓冲！",
        "呜～ 还请稍等，我需要[{}秒]来恢复状态！",
        "呀～ 这样的速度我有点跟不上，等我[{}秒]！",
        "哎呀，不要太快，我还需要[{}秒]调整！",
        "呜～ 请给我[{}秒]的冷却时间！",
        "呀～ 再给我[{}秒]，我会调整好的！"
    )

    val emptyResultMessage = listOf(
        "已经被榨干了，一滴都没有了",
        "已经被玩弄得一干二净，连一丝痕迹都没有了。",
        "这场欢愉已经结束，留下的只有空荡荡的回忆。",
        "已经被彻底享用，连一丝余温都不复存在。",
        "现在只剩下寂静，早已没有任何的欲望。",
        "所有的热情都被点燃过，留下的只是冷却后的余烬。",
        "一切都已被彻底消耗，连最后的温柔都不见了。",
        "现在已经光秃秃的，所有的享受都已成空。",
        "一切都已经结束，连最后的喘息声都已隐去。",
        "心底的渴望早已枯竭，再也无法找到任何满足。",
        "已经被榨得精疲力竭，连一丝香气都留不下。",
        "这身体已经不再敏感，仿佛被彻底冷落。",
        "所有的欢愉已被夺走，只剩下毫无生气的空壳。",
        "无尽的渴望已化为泡影，残留的只是失落。",
        "这一切都被耗尽，连最后的快感都消散无踪。",
        "心灵早已被撕扯，连一点刺激都找不到。",
        "已经被彻底放空，连一点余韵都没剩下。",
        "热情如火般燃烧，却在瞬间化为乌有。",
        "再也没有任何诱惑，只有无尽的疲惫。",
        "一切都已被掏空，留下的只有无奈与寂寞。",
        "已无欲无求，唯有冰冷的空虚在心底徘徊。",
    )

    val emptyResultMessageWithTag = listOf(
        "人家还不会 {} 这个姿势，你轻点好嘛！",
        "你就不能慢点吗？人家还在学习 {} 哦~",
        "慢点，慢点，人家还没掌握 {} 的技巧呢！",
        "人家刚刚学会 {}，别这么急好不好？",
        "你这样太快了，人家还没适应 {} 的感觉呢！",
        "等一下，人家还在琢磨 {} 的用法呢！",
        "人家还不会 {}，你要耐心点啊~",
        "轻点，人家还在尝试 {} 的乐趣呢！",
        "人家还不太熟悉 {}，别一下子就来这么猛！",
        "小心点，人家还在摸索 {} 的诀窍呢~",
    )

    val waitMessage = listOf(
        "en~，快了，就快粗来了，请再稍等一下~",
        "等一下哦，快到了，让我再喘口气~",
        "慢点慢点，马上就来，稍微忍耐一下~",
        "en~，快了，再给我一点时间，别急哦~",
        "稍等一下，快要到了，让我再调整一下~",
        "en~，再等等，快要爆发了，耐心点~",
        "快到了，再给我一点点时间，别着急哦~",
        "en~，快来了，稍微再等一等，别心急~",
        "等我一下，马上就能见到，耐心点~",
        "en~，快到了，快要见证高潮了，请稍等~",
    )
}