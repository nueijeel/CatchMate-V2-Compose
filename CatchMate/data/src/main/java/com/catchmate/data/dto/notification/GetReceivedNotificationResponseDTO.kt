package com.catchmate.data.dto.notification

import com.catchmate.data.dto.board.BoardDTO

data class GetReceivedNotificationResponseDTO(
    val notificationId: Long,
    val boardInfo: BoardDTO,
    val senderProfileImageUrl: String,
    val title: String,
    val body: String,
    val createdAt: String,
    val acceptStatus: String,
    val read: Boolean,
)
