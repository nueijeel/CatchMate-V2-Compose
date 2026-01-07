package com.catchmate.data.dto.board

data class GetUserBoardListResponseDTO(
    val boardInfoList: List<BoardDTO>,
    val totalPages: Int,
    val totalElements: Int,
    val isFirst: Boolean,
    val isLast: Boolean,
)
