package com.catchmate.domain.usecase.board

import com.catchmate.domain.model.board.GetBoardListResponse
import com.catchmate.domain.repository.BoardRepository
import javax.inject.Inject

class GetBoardListUseCase
    @Inject
    constructor(
        private val boardRepository: BoardRepository,
    ) {
        suspend fun getBoardList(
            gameStartDate: String?,
            maxPerson: Int?,
            preferredTeamIdList: Array<Int>?,
            page: Int?,
        ): Result<GetBoardListResponse> = boardRepository.getBoardList(gameStartDate, maxPerson, preferredTeamIdList, page)
    }
