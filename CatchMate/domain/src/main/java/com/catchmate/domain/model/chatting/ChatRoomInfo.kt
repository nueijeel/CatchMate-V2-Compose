package com.catchmate.domain.model.chatting

import com.catchmate.domain.model.board.Board

data class ChatRoomInfo(
    val chatRoomId: Long,
    val boardInfo: Board,
    val participantCount: Int,
    val lastMessageAt: String?,
    val lastMessageContent: String?,
    val chatRoomImage: String,
    val unreadMessageCount: Int,
    val isNewChatRoom: Boolean,
    val isNotificationEnabled: Boolean,
)
