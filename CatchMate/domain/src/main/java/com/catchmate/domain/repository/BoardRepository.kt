package com.catchmate.domain.repository

import com.catchmate.domain.model.board.DeleteBoardLikeResponse
import com.catchmate.domain.model.board.DeleteBoardResponse
import com.catchmate.domain.model.board.GetBoardListResponse
import com.catchmate.domain.model.board.GetBoardResponse
import com.catchmate.domain.model.board.GetLikedBoardResponse
import com.catchmate.domain.model.board.GetTempBoardResponse
import com.catchmate.domain.model.board.GetUserBoardListResponse
import com.catchmate.domain.model.board.PatchBoardLiftUpResponse
import com.catchmate.domain.model.board.PatchBoardRequest
import com.catchmate.domain.model.board.PatchBoardResponse
import com.catchmate.domain.model.board.PostBoardLikeResponse
import com.catchmate.domain.model.board.PostBoardRequest
import com.catchmate.domain.model.board.PostBoardResponse

interface BoardRepository {
    suspend fun postBoard(postBoardRequest: PostBoardRequest): Result<PostBoardResponse>

    suspend fun postBoardLike(boardId: Long): Result<PostBoardLikeResponse>

    suspend fun patchBoard(
        boardId: Long,
        patchBoardRequest: PatchBoardRequest,
    ): Result<PatchBoardResponse>

    suspend fun patchBoardLiftUp(boardId: Long): Result<PatchBoardLiftUpResponse>

    suspend fun getBoardList(
        gameStartDate: String?,
        maxPerson: Int?,
        preferredTeamIdList: Array<Int>?,
        page: Int?,
    ): Result<GetBoardListResponse>

    suspend fun getUserBoardList(
        userId: Long,
        page: Int,
    ): Result<GetUserBoardListResponse>

    suspend fun getBoard(boardId: Long): Result<GetBoardResponse>

    suspend fun getLikedBoard(page: Int): Result<GetLikedBoardResponse>

    suspend fun getTempBoard(): Result<GetTempBoardResponse>

    suspend fun deleteBoard(boardId: Long): Result<DeleteBoardResponse>

    suspend fun deleteBoardLike(boardId: Long): Result<DeleteBoardLikeResponse>
}
