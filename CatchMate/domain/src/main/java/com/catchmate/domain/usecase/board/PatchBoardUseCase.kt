package com.catchmate.domain.usecase.board

import com.catchmate.domain.model.board.PatchBoardRequest
import com.catchmate.domain.model.board.PatchBoardResponse
import com.catchmate.domain.repository.BoardRepository
import javax.inject.Inject

class PatchBoardUseCase
    @Inject
    constructor(
        private val boardRepository: BoardRepository,
    ) {
        suspend fun patchBoard(
            boardId: Long,
            patchBoardRequest: PatchBoardRequest,
        ): Result<PatchBoardResponse> = boardRepository.patchBoard(boardId, patchBoardRequest)
    }
