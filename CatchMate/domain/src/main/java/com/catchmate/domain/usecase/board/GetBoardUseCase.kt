package com.catchmate.domain.usecase.board

import com.catchmate.domain.model.board.GetBoardResponse
import com.catchmate.domain.repository.BoardRepository
import javax.inject.Inject

class GetBoardUseCase
    @Inject
    constructor(
        private val boardRepository: BoardRepository,
    ) {
        suspend fun getBoard(boardId: Long): Result<GetBoardResponse> = boardRepository.getBoard(boardId)
    }
