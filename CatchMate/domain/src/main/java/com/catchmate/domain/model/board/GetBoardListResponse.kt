package com.catchmate.domain.model.board

data class GetBoardListResponse(
    val boardInfoList: List<Board>,
    val totalPages: Int,
    val totalElements: Int,
    val isFirst: Boolean,
    val isLast: Boolean,
)
