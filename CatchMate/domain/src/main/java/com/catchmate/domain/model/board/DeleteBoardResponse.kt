package com.catchmate.domain.model.board

data class DeleteBoardResponse(
    val boardId: Long,
    val deletedAt: String,
)
