package com.catchmate.domain.usecase.board

import com.catchmate.domain.model.board.DeleteBoardResponse
import com.catchmate.domain.repository.BoardRepository
import javax.inject.Inject

class DeleteBoardUseCase
    @Inject
    constructor(
        private val boardRepository: BoardRepository,
    ) {
        suspend fun deleteBoard(boardId: Long): Result<DeleteBoardResponse> = boardRepository.deleteBoard(boardId)
    }
