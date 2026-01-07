package com.catchmate.domain.usecase.board

import com.catchmate.domain.model.board.GetTempBoardResponse
import com.catchmate.domain.repository.BoardRepository
import javax.inject.Inject

class GetTempBoardUseCase
    @Inject
    constructor(
        private val boardRepository: BoardRepository,
    ) {
        suspend fun getTempBoard(): Result<GetTempBoardResponse> = boardRepository.getTempBoard()
    }
