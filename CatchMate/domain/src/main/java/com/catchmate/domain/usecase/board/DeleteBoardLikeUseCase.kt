package com.catchmate.domain.usecase.board

import com.catchmate.domain.model.board.DeleteBoardLikeResponse
import com.catchmate.domain.repository.BoardRepository
import javax.inject.Inject

class DeleteBoardLikeUseCase
    @Inject
    constructor(
        private val boardRepository: BoardRepository,
    ) {
        suspend fun deleteBoardLike(boardId: Long): Result<DeleteBoardLikeResponse> = boardRepository.deleteBoardLike(boardId)
    }
