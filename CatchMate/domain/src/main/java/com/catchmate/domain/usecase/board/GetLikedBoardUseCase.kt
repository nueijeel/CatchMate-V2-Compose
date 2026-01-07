package com.catchmate.domain.usecase.board

import com.catchmate.domain.model.board.GetLikedBoardResponse
import com.catchmate.domain.repository.BoardRepository
import javax.inject.Inject

class GetLikedBoardUseCase
    @Inject
    constructor(
        private val boardRepository: BoardRepository,
    ) {
        suspend fun getLikedBoard(page: Int): Result<GetLikedBoardResponse> = boardRepository.getLikedBoard(page)
    }
