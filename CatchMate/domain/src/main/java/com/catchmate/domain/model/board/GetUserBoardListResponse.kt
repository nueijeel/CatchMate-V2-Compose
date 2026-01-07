package com.catchmate.domain.model.board

data class GetUserBoardListResponse(
    val boardInfoList: List<Board>,
    val totalPages: Int,
    val totalElements: Int,
    val isFirst: Boolean,
    val isLast: Boolean,
)
