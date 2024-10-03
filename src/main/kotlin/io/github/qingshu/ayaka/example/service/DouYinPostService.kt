package io.github.qingshu.ayaka.example.service

import com.fasterxml.jackson.databind.node.ObjectNode

/**
 * Copyright (c) 2024 qingshu.
 * This file is part of the example-ayaka project.
 *
 * This project is licensed under the AGPL-3.0 License.
 * See the LICENSE file for details.
 */
interface DouYinPostService {

    /**
     * 获取单个作品数据
     * @param id 作品 id
     */
    fun fetchOneVideo(id: String): ObjectNode

    /**
     * 获取用户主页作品数据
     * @param secUid 用户 sec_user_id
     * @param maxCursor 最大游标
     * @param count 最大数量
     */
    fun fetchUserPostVideos(secUid: String, maxCursor: Long = 0, count: Int = 20): ObjectNode

    /**
     * 获取用户喜欢作品数据
     * @param secUid 用户 sec_user_id
     * @param maxCursor 最大游标
     * @param count 最大数量
     */
    fun fetchUserLikeVideos(secUid: String, maxCursor: Long = 0, count: Int = 20): ObjectNode

    /**
     * 获取用户收藏作品数据
     * @param cookie 用户网页版抖音 Cookie(此接口需要用户提供自己的 Cookie)
     * @param maxCursor 最大游标
     * @param count 最大数量
     */
    fun fetchUserCollectionVideos(cookie: String, maxCursor: Long = 0, count: Int = 20): ObjectNode

    /**
     * 获取用户合辑作品数据
     * @param mixId 合辑id
     * @param maxCursor 最大游标
     * @param count 最大数量
     */
    fun fetchUserMixVideos(mixId: String, maxCursor: Long = 0, count: Int = 20): ObjectNode

    /**
     * 获取用户直播流数据
     * @param webcastId 直播间webcast_id
     */
    fun fetchUserLiveVideos(webcastId: String): ObjectNode

    /**
     * 获取指定用户的直播流数据
     * @param roomId 直播间 room_id
     */
    fun fetchUserLiveVideosByRoomId(roomId: String): ObjectNode

    /**
     * 获取直播间送礼用户排行榜
     * @param roomId 直播间 room_id
     * @param rankType 排行类型，默认为30不用修改。
     */
    fun fetchLiveGiftRanking(roomId: String, rankType: Int = 30): ObjectNode

    /**
     * 抖音直播间商品信息
     * @param cookie 用户网页版抖音 Cookie(此接口需要用户提供自己的 Cookie，如获取失败请手动过一次验证码)
     * @param roomId 直播间 room_id
     * @param authorId 作者 id
     * @param limit 数量
     */
    fun fetchLiveRoomProductResult(cookie: String, roomId: String, authorId: String, limit: Int): ObjectNode

    /**
     * 获取指定用户的信息
     * @param secUid 用户 sec_user_id
     */
    fun handlerUserProfile(secUid: String): ObjectNode

    /**
     * 获取单个视频评论数据
     * @param id  作品id
     * @param maxCursor 游标
     * @param count 数量
     */
    fun fetchVideoComments(id: String, maxCursor: Long = 0, count: Int = 20): ObjectNode

    /**
     * 获取指定视频的评论回复数据
     * @param itemId 作品id
     * @param commentId 评论id
     * @param cursor 游标
     * @param count 数量
     */
    fun fetchVideoCommentReplies(itemId: String, commentId: String, cursor: Long = 0, count: Int = 20): ObjectNode

    /**
     * 生成真实 msToken
     */
    fun generateRealmsToken(): ObjectNode

    /**
     * 生成 ttwid
     */
    fun generateTTWId(): ObjectNode

    /**
     * 生成 verify_fp
     */
    fun generateVerifyFp(): ObjectNode

    /**
     * 生成s_v_web_id
     */
    fun generateSVWebId(): ObjectNode

    /**
     * 使用接口网址生成X-Bogus参数
     * @param url 接口网址
     * @param userAgent 用户代理，暂时不支持自定义，直接使用默认值即可。
     */
    fun generateXBogus(url: String, userAgent: String): ObjectNode

    /**
     * 使用接口网址生成A-Bogus参数
     * @param url 接口网址
     * @param userAgent 用户代理，暂时不支持自定义，直接使用默认值即可。
     */
    fun generateABogus(url: String, userAgent: String): ObjectNode

    /**
     * 提取单个用户id
     * @param url  用户主页链接
     */
    fun getSecUserId(url: String): ObjectNode

    /**
     * 提取列表用户id
     * @param url 用户主页链接列表
     */
    fun getAllSecUserId(url: List<String>): ObjectNode

    /**
     * 提取单个作品id
     * @param url 作品链接
     */
    fun getVideoId(url: String): ObjectNode

    /**
     * 提取列表作品id
     * @param url 作品链接列表
     */
    fun getAllVideoId(url: List<String>): ObjectNode

    /**
     * 提取列表直播间号
     * @param url 直播间链接
     */
    fun getWebcastId(url: String): ObjectNode

    /**
     * 提取列表直播间号
     * @param url 直播间链接列表
     */
    fun getAllWebcastId(url: List<String>): ObjectNode
}