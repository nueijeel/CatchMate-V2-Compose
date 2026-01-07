package com.catchmate.domain.usecase.board

import com.catchmate.domain.model.board.GetUserBoardListResponse
import com.catchmate.domain.repository.BoardRepository
import javax.inject.Inject

class GetUserBoardListUseCase
    @Inject
    constructor(
        private val boardRepository: BoardRepository,
    ) {
        suspend fun getUserBoardList(
            userId: Long,
            page: Int,
        ): Result<GetUserBoardListResponse> = boardRepository.getUserBoardList(userId, page)
    }
