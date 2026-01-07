package com.catchmate.domain.model.support

data class NoticeInfo(
    val noticeId: Long,
    val title: String,
    val content: String,
    val userInfo: AdminUserInfo,
    val createdAt: String,
    val updatedAt: String,
)
