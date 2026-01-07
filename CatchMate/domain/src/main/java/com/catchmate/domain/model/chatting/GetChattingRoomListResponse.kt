package com.catchmate.domain.model.chatting

data class GetChattingRoomListResponse(
    val chatRoomInfoList: List<ChatRoomInfo>,
    val totalPages: Int,
    val totalElements: Int,
    val isFirst: Boolean,
    val isLast: Boolean,
)
