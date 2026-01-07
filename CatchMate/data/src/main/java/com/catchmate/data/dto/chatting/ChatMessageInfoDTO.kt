package com.catchmate.data.dto.chatting

data class ChatMessageInfoDTO(
    val id: ChatMessageIdDTO,
    val chatMessageId: String,
    val roomId: Long? = null,
    val content: String,
    val senderId: Long,
    val messageType: String,
)
