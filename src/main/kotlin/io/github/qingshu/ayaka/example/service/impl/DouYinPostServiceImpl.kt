package io.github.qingshu.ayaka.example.service.impl

import com.fasterxml.jackson.databind.node.ObjectNode
import io.github.qingshu.ayaka.example.service.DouYinPostService
import io.github.qingshu.ayaka.example.utils.NetUtils
import io.github.qingshu.ayaka.utils.mapper
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import org.springframework.stereotype.Service

/**
 * Copyright (c) 2024 qingshu.
 * This file is part of the example-ayaka project.
 *
 * This project is licensed under the AGPL-3.0 License.
 * See the LICENSE file for details.
 */
// @formatter:off
@Service
class DouYinPostServiceImpl : DouYinPostService {

    private val baseUrl = "http://117.72.12.207/api/douyin/web"

    /**
     * 获取单个作品数据
     * @param id 作品 id
     */
    override fun fetchOneVideo(id: String): ObjectNode {
        val mUrl = buildUrl("fetch_one_video", mapOf("aweme_id" to id))
        return performRequest(mUrl.toString())
    }

    /**
     * 获取用户主页作品数据
     * @param secUid 用户 sec_user_id
     * @param maxCursor 最大游标
     * @param count 最大数量
     */
    override fun fetchUserPostVideos(secUid: String, maxCursor: Long, count: Int): ObjectNode {
        val mUrl = buildUrl("fetch_user_post_videos", mapOf(
            "sec_user_id" to secUid,
            "max_cursor" to maxCursor.toString(),
            "count" to count.toString(),
        ))
        return performRequest(mUrl.toString())
    }

    /**
     * 获取用户喜欢作品数据
     * @param secUid 用户 sec_user_id
     * @param maxCursor 最大游标
     * @param count 最大数量
     */
    override fun fetchUserLikeVideos(secUid: String, maxCursor: Long, count: Int): ObjectNode {
        val mUrl = buildUrl("fetch_user_like_videos", mapOf(
            "sec_user_id" to secUid,
            "max_cursor" to maxCursor.toString(),
            "count" to count.toString(),
        ))
        return performRequest(mUrl.toString())
    }

    /**
     * 获取用户收藏作品数据
     * @param cookie 用户网页版抖音 Cookie(此接口需要用户提供自己的 Cookie)
     * @param maxCursor 最大游标
     * @param count 最大数量
     */
    override fun fetchUserCollectionVideos(cookie: String, maxCursor: Long, count: Int): ObjectNode {
        val mUrl = buildUrl("fetch_user_collection_videos", mapOf(
            "cookie" to cookie,
            "max_cursor" to maxCursor.toString(),
            "count" to count.toString(),
        ))
        return performRequest(mUrl.toString())
    }

    /**
     * 获取用户合辑作品数据
     * @param mixId 合辑id
     * @param maxCursor 最大游标
     * @param count 最大数量
     */
    override fun fetchUserMixVideos(mixId: String, maxCursor: Long, count: Int): ObjectNode {
        val mUrl = buildUrl("fetch_user_mix_videos", mapOf(
            "mix_id" to mixId,
            "max_cursor" to maxCursor.toString(),
            "count" to count.toString(),
        ))
        return performRequest(mUrl.toString())
    }

    /**
     * 获取用户直播流数据
     * @param webcastId 直播间webcast_id
     */
    override fun fetchUserLiveVideos(webcastId: String): ObjectNode {
        val mUrl = buildUrl("fetch_user_live_videos", mapOf(
            "webcast_id" to webcastId
        ))
        return performRequest(mUrl.toString())
    }

    /**
     * 获取指定用户的直播流数据
     * @param roomId 直播间 room_id
     */
    override fun fetchUserLiveVideosByRoomId(roomId: String): ObjectNode {
        val mUrl = buildUrl("fetch_user_live_videos", mapOf("room_id" to roomId))
        return performRequest(mUrl.toString())
    }

    /**
     * 获取直播间送礼用户排行榜
     * @param roomId 直播间 room_id
     * @param rankType 排行类型，默认为30不用修改。
     */
    override fun fetchLiveGiftRanking(roomId: String, rankType: Int): ObjectNode {
        val mUrl = buildUrl("fetch_live_gift_ranking", mapOf(
            "room_id" to roomId,
            "rank_type" to rankType.toString()
        ))
        return performRequest(mUrl.toString())
    }

    /**
     * 抖音直播间商品信息
     * @param cookie 用户网页版抖音 Cookie(此接口需要用户提供自己的 Cookie，如获取失败请手动过一次验证码)
     * @param roomId 直播间 room_id
     * @param authorId 作者 id
     * @param limit 数量
     */
    override fun fetchLiveRoomProductResult(cookie: String, roomId: String, authorId: String, limit: Int): ObjectNode {
        val mUrl = buildUrl("fetch_live_room_product_result", mapOf(
            "cookie" to cookie,
            "room_id" to roomId,
            "author_id" to authorId,
            "limit" to limit.toString(),
        ))
        return performRequest(mUrl.toString())
    }

    /**
     * 获取指定用户的信息
     * @param secUid 用户 sec_user_id
     */
    override fun handlerUserProfile(secUid: String): ObjectNode {
        val mUrl = buildUrl("handler_user_profile", mapOf("sec_user_id" to secUid))
        return performRequest(mUrl.toString())
    }

    /**
     * 获取单个视频评论数据
     * @param id  作品id
     * @param maxCursor 游标
     * @param count 数量
     */
    override fun fetchVideoComments(id: String, maxCursor: Long, count: Int): ObjectNode {
        val mUrl = buildUrl("fetch_video_comments", mapOf(
            "aweme_id" to id,
            "cursor" to maxCursor.toString(),
            "count" to count.toString(),
        ))
        return performRequest(mUrl.toString())
    }

    /**
     * 获取指定视频的评论回复数据
     * @param itemId 作品id
     * @param commentId 评论id
     * @param cursor 游标
     * @param count 数量
     */
    override fun fetchVideoCommentReplies(itemId: String, commentId: String, cursor: Long, count: Int): ObjectNode {
        val mUrl = buildUrl("fetch_video_comment_replies", mapOf(
            "item_id" to itemId,
            "comment_id" to commentId,
            "cursor" to cursor.toString(),
            "count" to count.toString(),
        ))
        return performRequest(mUrl.toString())
    }

    /**
     * 生成真实 msToken
     */
    override fun generateRealmsToken(): ObjectNode {
        val mUrl = buildUrl("generate_real_token")
        return performRequest(mUrl.toString())
    }

    /**
     * 生成 ttwid
     */
    override fun generateTTWId(): ObjectNode {
        val mUrl = buildUrl("generate_ttwid")
        return performRequest(mUrl.toString())
    }

    /**
     * 生成 verify_fp
     */
    override fun generateVerifyFp(): ObjectNode {
        val mUrl = buildUrl("generate_verify_fp")
        return performRequest(mUrl.toString())
    }

    /**
     * 生成s_v_web_id
     */
    override fun generateSVWebId(): ObjectNode {
        val mUrl = buildUrl("generate_s_v_web_id")
        return performRequest(mUrl.toString())
    }

    /**
     * 使用接口网址生成X-Bogus参数
     * @param url 接口网址
     * @param userAgent 用户代理，暂时不支持自定义，直接使用默认值即可。
     */
    override fun generateXBogus(url: String, userAgent: String): ObjectNode {
        val mUrl = buildUrl("generate_x_bogus", mapOf(
            "url" to url,
            "user_agent" to userAgent,
        ))
        return performRequest(mUrl.toString())
    }

    /**
     * 使用接口网址生成A-Bogus参数
     * @param url 接口网址
     * @param userAgent 用户代理，暂时不支持自定义，直接使用默认值即可。
     */
    override fun generateABogus(url: String, userAgent: String): ObjectNode {
        val mUrl = buildUrl("generate_a_bogus", mapOf(
            "url" to url,
            "user_agent" to userAgent,
        ))
        return performRequest(mUrl.toString())
    }

    /**
     * 提取单个用户id
     * @param url  用户主页链接
     */
    override fun getSecUserId(url: String): ObjectNode {
        val mUrl = buildUrl("get_sec_user_id", mapOf("url" to url))
        return performRequest(mUrl.toString())
    }

    /**
     * 提取列表用户id
     * @param url 用户主页链接列表
     */
    override fun getAllSecUserId(url: List<String>): ObjectNode {
        val mUrl = buildUrl("get_all_sec_user_id")
        return performWithPost(mUrl.toString(), mapper.writeValueAsString(url))
    }

    /**
     * 提取单个作品id
     * @param url 作品链接
     */
    override fun getVideoId(url: String): ObjectNode {
        val mUrl = buildUrl("get_aweme_id", mapOf("url" to url))
        return performRequest(mUrl.toString())
    }

    /**
     * 提取列表作品id
     * @param url 作品链接列表
     */
    override fun getAllVideoId(url: List<String>): ObjectNode {
        val mUrl = buildUrl("get_all_aweme_id")
        return performWithPost(mUrl.toString(), mapper.writeValueAsString(url))
    }

    /**
     * 提取列表直播间号
     * @param url 直播间链接
     */
    override fun getWebcastId(url: String): ObjectNode {
        val mUrl = buildUrl("get_webcast_id", mapOf("url" to url))
        return performRequest(mUrl.toString())
    }

    /**
     * 提取列表直播间号
     * @param url 直播间链接列表
     */
    override fun getAllWebcastId(url: List<String>): ObjectNode {
        val mUrl = buildUrl("get_all_webcast_id")
        return performWithPost(mUrl.toString(), mapper.writeValueAsString(url))
    }

    private fun performRequest(url: String): ObjectNode {
        NetUtils.get(url).use { resp ->
            val responseBody = resp.body?.string()
            return mapper.readTree(responseBody) as ObjectNode
        }
    }

    private fun performWithPost(url: String, params: String): ObjectNode {
        NetUtils.post(url, params).use { resp ->
            val responseBody = resp.body?.string()
            return mapper.readTree(responseBody) as ObjectNode
        }
    }

    private fun buildUrl(path: String, params: Map<String, String> = emptyMap()): HttpUrl? {
        return baseUrl.toHttpUrlOrNull()?.newBuilder()
            ?.addPathSegment(path)
            ?.apply {
                params.forEach { (key, value) -> addQueryParameter(key, value) }
            }
            ?.build()
    }
}
// @formatter:on

/*// example
fun main(args: Array<String>) {
    val url = "https://v.douyin.com/iBjRr8Yc/"
    val service = DouYinPostServiceImpl()

    val result = service.getVideoId(url).let {
        if (it.has("data")) return@let it["data"].asText()
        return@let ""
    }

    val videoInfo = service.fetchOneVideo(result)
    println(videoInfo)
}*/
