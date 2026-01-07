package com.catchmate.domain.model.board

data class GetLikedBoardResponse(
    val boardInfoList: List<Board>,
    val totalPages: Int,
    val totalElements: Int,
    val isFirst: Boolean,
    val isLast: Boolean,
)
