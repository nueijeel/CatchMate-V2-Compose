package com.catchmate.data.dto.chatting

import com.catchmate.data.dto.board.BoardDTO

data class ChatRoomInfoDTO(
    val chatRoomId: Long,
    val boardInfo: BoardDTO,
    val participantCount: Int,
    val lastMessageAt: String?,
    val lastMessageContent: String?,
    val chatRoomImage: String,
    val unreadMessageCount: Int,
    val isNewChatRoom: Boolean,
    val isNotificationEnabled: Boolean,
)
