package com.catchmate.data.dto.support

data class NoticeInfoDTO(
    val noticeId: Long,
    val title: String,
    val content: String,
    val userInfo: AdminUserInfoDTO,
    val createdAt: String,
    val updatedAt: String,
)
