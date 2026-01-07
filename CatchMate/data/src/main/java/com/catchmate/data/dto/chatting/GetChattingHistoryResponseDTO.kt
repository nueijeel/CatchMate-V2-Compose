package com.catchmate.data.dto.chatting

data class GetChattingHistoryResponseDTO(
    val chatMessageInfoList: List<ChatMessageInfoDTO>,
    val isFirst: Boolean,
    val isLast: Boolean,
    val lastMessageId: String?,
)
