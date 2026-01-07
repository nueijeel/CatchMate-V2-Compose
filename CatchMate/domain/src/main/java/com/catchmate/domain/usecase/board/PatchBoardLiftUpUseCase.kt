package com.catchmate.domain.usecase.board

import com.catchmate.domain.model.board.PatchBoardLiftUpResponse
import com.catchmate.domain.repository.BoardRepository
import javax.inject.Inject

class PatchBoardLiftUpUseCase
    @Inject
    constructor(
        private val boardRepository: BoardRepository,
    ) {
        suspend fun patchBoardLiftUp(boardId: Long): Result<PatchBoardLiftUpResponse> = boardRepository.patchBoardLiftUp(boardId)
    }
