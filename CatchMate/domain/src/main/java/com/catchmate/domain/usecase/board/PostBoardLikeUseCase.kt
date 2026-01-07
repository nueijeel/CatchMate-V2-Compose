package com.catchmate.domain.usecase.board

import com.catchmate.domain.model.board.PostBoardLikeResponse
import com.catchmate.domain.repository.BoardRepository
import javax.inject.Inject

class PostBoardLikeUseCase
    @Inject
    constructor(
        private val boardRepository: BoardRepository,
    ) {
        suspend fun postBoardLike(boardId: Long): Result<PostBoardLikeResponse> = boardRepository.postBoardLike(boardId)
    }
