package com.catchmate.data.mapper

import com.catchmate.data.dto.board.BoardDTO
import com.catchmate.data.dto.board.DeleteBoardLikeResponseDTO
import com.catchmate.data.dto.board.DeleteBoardResponseDTO
import com.catchmate.data.dto.board.GetBoardListResponseDTO
import com.catchmate.data.dto.board.GetBoardResponseDTO
import com.catchmate.data.dto.board.GetLikedBoardResponseDTO
import com.catchmate.data.dto.board.GetTempBoardResponseDTO
import com.catchmate.data.dto.board.GetUserBoardListResponseDTO
import com.catchmate.data.dto.board.PatchBoardLiftUpResponseDTO
import com.catchmate.data.dto.board.PatchBoardRequestDTO
import com.catchmate.data.dto.board.PatchBoardResponseDTO
import com.catchmate.data.dto.board.PostBoardLikeResponseDTO
import com.catchmate.data.dto.board.PostBoardRequestDTO
import com.catchmate.data.dto.board.PostBoardResponseDTO
import com.catchmate.data.dto.enroll.GameInfoDTO
import com.catchmate.data.dto.enroll.UserInfoDTO
import com.catchmate.data.dto.user.FavoriteClubDTO
import com.catchmate.domain.model.board.Board
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
import com.catchmate.domain.model.enroll.GameInfo
import com.catchmate.domain.model.enroll.UserInfo
import com.catchmate.domain.model.user.FavoriteClub

object BoardMapper {
    fun toPostBoardRequestDTO(request: PostBoardRequest): PostBoardRequestDTO =
        PostBoardRequestDTO(
            title = request.title,
            content = request.content,
            maxPerson = request.maxPerson,
            cheerClubId = request.cheerClubId,
            preferredGender = request.preferredGender,
            preferredAgeRange = request.preferredAgeRange,
            gameRequest = toGameRequestDTO(request.gameRequest),
            isCompleted = request.isCompleted,
        )

    private fun toGameRequestDTO(request: GameInfo): GameInfoDTO =
        GameInfoDTO(
            homeClubId = request.homeClubId,
            awayClubId = request.awayClubId,
            gameStartDate = request.gameStartDate,
            location = request.location,
        )

    fun toPostBoardResponse(dto: PostBoardResponseDTO): PostBoardResponse =
        PostBoardResponse(
            boardId = dto.boardId,
            title = dto.title,
            content = dto.content,
            cheerClubId = dto.cheerClubId,
            currentPerson = dto.currentPerson,
            maxPerson = dto.maxPerson,
            preferredGender = dto.preferredGender,
            preferredAgeRange = dto.preferredAgeRange,
            gameInfo = toGameInfo(dto.gameInfo),
            liftUpDate = dto.liftUpDate,
            userInfo = toUserInfo(dto.userInfo),
        )

    private fun toGameInfo(dto: GameInfoDTO): GameInfo =
        GameInfo(
            homeClubId = dto.homeClubId,
            awayClubId = dto.awayClubId,
            gameStartDate = dto.gameStartDate,
            location = dto.location,
        )

    private fun toUserInfo(dto: UserInfoDTO): UserInfo =
        UserInfo(
            userId = dto.userId,
            email = dto.email,
            profileImageUrl = dto.profileImageUrl,
            gender = dto.gender,
            allAlarm = dto.allAlarm,
            chatAlarm = dto.chatAlarm,
            enrollAlarm = dto.enrollAlarm,
            eventAlarm = dto.eventAlarm,
            nickName = dto.nickName,
            favoriteClub = toFavoriteClub(dto.favoriteClub),
            birthDate = dto.birthDate,
            watchStyle = dto.watchStyle,
        )

    fun toFavoriteClub(dto: FavoriteClubDTO): FavoriteClub =
        FavoriteClub(
            id = dto.id,
            name = dto.name,
            homeStadium = dto.homeStadium,
            region = dto.region,
        )

    fun toPatchBoardRequestDTO(request: PatchBoardRequest): PatchBoardRequestDTO =
        PatchBoardRequestDTO(
            title = request.title,
            content = request.content,
            maxPerson = request.maxPerson,
            cheerClubId = request.cheerClubId,
            preferredGender = request.preferredGender,
            preferredAgeRange = request.preferredAgeRange,
            gameRequest = toGameRequestDTO(request.gameRequest),
            isCompleted = request.isCompleted,
        )

    fun toPatchBoardResponse(responseDTO: PatchBoardResponseDTO): PatchBoardResponse =
        PatchBoardResponse(
            boardId = responseDTO.boardId,
            title = responseDTO.title,
            content = responseDTO.content,
            cheerClubId = responseDTO.cheerClubId,
            currentPerson = responseDTO.currentPerson,
            maxPerson = responseDTO.maxPerson,
            preferredGender = responseDTO.preferredGender,
            preferredAgeRange = responseDTO.preferredAgeRange,
            gameInfo = toGameInfo(responseDTO.gameInfo),
            liftUpDate = responseDTO.liftUpDate,
            userInfo = toUserInfo(responseDTO.userInfo),
            buttonStatus = responseDTO.buttonStatus,
            bookMarked = responseDTO.bookMarked,
        )

    fun toPatchBoardLiftUpResponse(dto: PatchBoardLiftUpResponseDTO): PatchBoardLiftUpResponse =
        PatchBoardLiftUpResponse(
            state = dto.state,
            remainTime = dto.remainTime,
        )

    fun toGetBoardListResponse(responseDTO: GetBoardListResponseDTO): GetBoardListResponse =
        GetBoardListResponse(
            boardInfoList = responseDTO.boardInfoList.map { toBoard(it) },
            totalPages = responseDTO.totalPages,
            totalElements = responseDTO.totalElements,
            isFirst = responseDTO.isFirst,
            isLast = responseDTO.isLast,
        )

    fun toBoard(dto: BoardDTO): Board =
        Board(
            boardId = dto.boardId,
            title = dto.title,
            content = dto.content,
            cheerClubId = dto.cheerClubId,
            currentPerson = dto.currentPerson,
            maxPerson = dto.maxPerson,
            preferredGender = dto.preferredGender,
            preferredAgeRange = dto.preferredAgeRange,
            gameInfo = toGameInfo(dto.gameInfo),
            liftUpDate = dto.liftUpDate,
            userInfo = toUserInfo(dto.userInfo),
            buttonStatus = dto.buttonStatus,
            chatRoomId = dto.chatRoomId,
            bookMarked = dto.bookMarked,
        )

    fun toGetUserBoardListResponse(dto: GetUserBoardListResponseDTO): GetUserBoardListResponse =
        GetUserBoardListResponse(
            boardInfoList = dto.boardInfoList.map { toBoard(it) },
            totalPages = dto.totalPages,
            totalElements = dto.totalElements,
            isFirst = dto.isFirst,
            isLast = dto.isLast,
        )

    fun toGetBoardResponse(responseDTO: GetBoardResponseDTO): GetBoardResponse =
        GetBoardResponse(
            boardId = responseDTO.boardId,
            title = responseDTO.title,
            content = responseDTO.content,
            cheerClubId = responseDTO.cheerClubId,
            currentPerson = responseDTO.currentPerson,
            maxPerson = responseDTO.maxPerson,
            preferredGender = responseDTO.preferredGender,
            preferredAgeRange = responseDTO.preferredAgeRange,
            liftUpDate = responseDTO.liftUpDate,
            gameInfo = toGameInfo(responseDTO.gameInfo),
            userInfo = toUserInfo(responseDTO.userInfo),
            buttonStatus = responseDTO.buttonStatus,
            chatRoomId = responseDTO.chatRoomId,
            bookMarked = responseDTO.bookMarked,
        )

    fun toGetTempBoardResponse(dto: GetTempBoardResponseDTO): GetTempBoardResponse =
        GetTempBoardResponse(
            boardId = dto.boardId,
            title = dto.title,
            content = dto.content,
            cheerClubId = dto.cheerClubId,
            currentPerson = dto.currentPerson,
            maxPerson = dto.maxPerson,
            preferredGender = dto.preferredGender,
            preferredAgeRange = dto.preferredAgeRange,
            gameInfo = toGameInfo(dto.gameInfo),
            liftUpDate = dto.liftUpDate,
            userInfo = toUserInfo(dto.userInfo),
        )

    fun toDeleteBoardResponse(dto: DeleteBoardResponseDTO): DeleteBoardResponse =
        DeleteBoardResponse(
            boardId = dto.boardId,
            deletedAt = dto.deletedAt,
        )

    fun toGetLikedBoardResponse(responseDTO: GetLikedBoardResponseDTO): GetLikedBoardResponse =
        GetLikedBoardResponse(
            boardInfoList = responseDTO.boardInfoList.map { toBoard(it) },
            totalPages = responseDTO.totalPages,
            totalElements = responseDTO.totalElements,
            isFirst = responseDTO.isFirst,
            isLast = responseDTO.isLast,
        )

    fun toPostBoardLikeResponse(dto: PostBoardLikeResponseDTO): PostBoardLikeResponse = PostBoardLikeResponse(dto.state)

    fun toDeleteBoardLikeResponse(dto: DeleteBoardLikeResponseDTO): DeleteBoardLikeResponse = DeleteBoardLikeResponse(dto.state)
}
