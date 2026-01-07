package com.catchmate.data.repository

import com.catchmate.data.datasource.remote.BoardService
import com.catchmate.data.datasource.remote.RetrofitClient
import com.catchmate.data.mapper.BoardMapper
import com.catchmate.data.util.ApiResponseHandleUtil.apiCall
import com.catchmate.domain.exception.BlockedUserBoardException
import com.catchmate.domain.exception.BookmarkFailureException
import com.catchmate.domain.exception.NonExistentTempBoardException
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
import com.catchmate.domain.repository.BoardRepository
import javax.inject.Inject

class BoardRepositoryImpl
    @Inject
    constructor(
        retrofitClient: RetrofitClient,
    ) : BoardRepository {
        private val boardApi = retrofitClient.createApi<BoardService>()
        private val tag = "BoardRepo"

        override suspend fun postBoard(postBoardRequest: PostBoardRequest): Result<PostBoardResponse> =
            apiCall(
                tag = this.tag,
                apiFunction = { boardApi.postBoard(BoardMapper.toPostBoardRequestDTO(postBoardRequest)) },
                transform = { BoardMapper.toPostBoardResponse(it!!) },
            )

        override suspend fun postBoardLike(boardId: Long): Result<PostBoardLikeResponse> =
            apiCall(
                tag = this.tag,
                apiFunction = { boardApi.postBoardLike(boardId) },
                transform = { BoardMapper.toPostBoardLikeResponse(it!!) },
                errorHandler = { response, jsonObject ->
                    if (response.code() == 400) {
                        val message = jsonObject.getString("message")
                        BookmarkFailureException(message)
                    } else {
                        Exception("$jsonObject")
                    }
                },
            )

        override suspend fun patchBoard(
            boardId: Long,
            patchBoardRequest: PatchBoardRequest,
        ): Result<PatchBoardResponse> =
            apiCall(
                tag = this.tag,
                apiFunction = { boardApi.patchBoard(boardId, BoardMapper.toPatchBoardRequestDTO(patchBoardRequest)) },
                transform = { BoardMapper.toPatchBoardResponse(it!!) },
            )

        override suspend fun patchBoardLiftUp(boardId: Long): Result<PatchBoardLiftUpResponse> =
            apiCall(
                tag = this.tag,
                apiFunction = { boardApi.patchBoardLiftUp(boardId) },
                transform = { BoardMapper.toPatchBoardLiftUpResponse(it!!) },
            )

        override suspend fun getBoardList(
            gameStartDate: String?,
            maxPerson: Int?,
            preferredTeamIdList: Array<Int>?,
            page: Int?,
        ): Result<GetBoardListResponse> =
            apiCall(
                tag = this.tag,
                apiFunction = { boardApi.getBoardList(gameStartDate, maxPerson, preferredTeamIdList, page) },
                transform = { BoardMapper.toGetBoardListResponse(it!!) },
            )

        override suspend fun getUserBoardList(
            userId: Long,
            page: Int,
        ): Result<GetUserBoardListResponse> =
            apiCall(
                tag = this.tag,
                apiFunction = { boardApi.getUserBoardList(userId, page) },
                transform = { BoardMapper.toGetUserBoardListResponse(it!!) },
            )

        override suspend fun getBoard(boardId: Long): Result<GetBoardResponse> =
            apiCall(
                tag = this.tag,
                apiFunction = { boardApi.getBoard(boardId) },
                transform = { BoardMapper.toGetBoardResponse(it!!) },
                errorHandler = { response, jsonObject ->
                    if (response.code() == 400) {
                        val message = jsonObject.getString("message")
                        BlockedUserBoardException(message)
                    } else {
                        Exception("$jsonObject")
                    }
                },
            )

        override suspend fun getLikedBoard(page: Int): Result<GetLikedBoardResponse> =
            apiCall(
                tag = this.tag,
                apiFunction = { boardApi.getLikedBoard(page) },
                transform = { BoardMapper.toGetLikedBoardResponse(it!!) },
            )

        override suspend fun getTempBoard(): Result<GetTempBoardResponse> =
            apiCall(
                tag = this.tag,
                apiFunction = { boardApi.getTempBoard() },
                transform = { BoardMapper.toGetTempBoardResponse(it!!) },
                errorHandler = { response, jsonObject ->
                    if (response.code() == 404) {
                        NonExistentTempBoardException("$jsonObject")
                    } else {
                        Exception("$jsonObject")
                    }
                },
            )

        override suspend fun deleteBoard(boardId: Long): Result<DeleteBoardResponse> =
            apiCall(
                tag = this.tag,
                apiFunction = { boardApi.deleteBoard(boardId) },
                transform = { BoardMapper.toDeleteBoardResponse(it!!) },
            )

        override suspend fun deleteBoardLike(boardId: Long): Result<DeleteBoardLikeResponse> =
            apiCall(
                tag = this.tag,
                apiFunction = { boardApi.deleteBoardLike(boardId) },
                transform = { BoardMapper.toDeleteBoardLikeResponse(it!!) },
            )
    }
