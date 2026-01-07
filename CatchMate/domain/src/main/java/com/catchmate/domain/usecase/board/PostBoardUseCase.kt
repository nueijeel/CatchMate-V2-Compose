package com.catchmate.domain.usecase.board

import com.catchmate.domain.model.board.PostBoardRequest
import com.catchmate.domain.model.board.PostBoardResponse
import com.catchmate.domain.repository.BoardRepository
import javax.inject.Inject

class PostBoardUseCase
    @Inject
    constructor(
        private val boardRepository: BoardRepository,
    ) {
        suspend fun postBoard(postBoardRequest: PostBoardRequest): Result<PostBoardResponse> = boardRepository.postBoard(postBoardRequest)
    }
