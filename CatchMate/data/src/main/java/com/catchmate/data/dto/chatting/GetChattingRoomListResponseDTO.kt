package com.catchmate.data.dto.chatting

data class GetChattingRoomListResponseDTO(
    val chatRoomInfoList: List<ChatRoomInfoDTO>,
    val totalPages: Int,
    val totalElements: Int,
    val isFirst: Boolean,
    val isLast: Boolean,
)
