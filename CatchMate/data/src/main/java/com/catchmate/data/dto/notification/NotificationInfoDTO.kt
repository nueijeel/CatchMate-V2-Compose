package com.catchmate.data.dto.notification

import com.catchmate.data.dto.board.BoardDTO

data class NotificationInfoDTO(
    val notificationId: Long,
    val boardInfo: BoardDTO?,
    val inquiryInfo: InquiryDTO?,
    val senderProfileImageUrl: String?,
    val title: String,
    val body: String,
    val createdAt: String,
    val acceptStatus: String?,
    val read: Boolean,
)
