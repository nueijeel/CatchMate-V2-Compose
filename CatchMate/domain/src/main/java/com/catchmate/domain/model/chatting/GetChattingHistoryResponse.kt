package com.catchmate.domain.model.chatting

data class GetChattingHistoryResponse(
    val chatMessageInfoList: List<ChatMessageInfo>,
    val isFirst: Boolean,
    val isLast: Boolean,
    val lastMessageId: String?,
)
