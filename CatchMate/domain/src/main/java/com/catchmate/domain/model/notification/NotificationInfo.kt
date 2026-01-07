package com.catchmate.domain.model.notification

import com.catchmate.domain.model.board.Board

data class NotificationInfo(
    val notificationId: Long,
    val boardInfo: Board?,
    val inquiryInfo: Inquiry?,
    val senderProfileImageUrl: String?,
    val title: String,
    val body: String,
    val createdAt: String,
    val acceptStatus: String?,
    var read: Boolean,
)
