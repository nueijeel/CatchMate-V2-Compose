package com.catchmate.data.mapper

import com.catchmate.data.dto.enroll.AllReceivedEnrollInfoResponseDTO
import com.catchmate.data.dto.enroll.DeleteEnrollResponseDTO
import com.catchmate.data.dto.enroll.EnrollBoardInfoDTO
import com.catchmate.data.dto.enroll.EnrollInfoDTO
import com.catchmate.data.dto.enroll.GameInfoDTO
import com.catchmate.data.dto.enroll.GetAllReceivedEnrollResponseDTO
import com.catchmate.data.dto.enroll.GetEnrollNewCountResponseDTO
import com.catchmate.data.dto.enroll.GetReceivedEnrollResponseDTO
import com.catchmate.data.dto.enroll.GetRequestedEnrollListResponseDTO
import com.catchmate.data.dto.enroll.GetRequestedEnrollResponseDTO
import com.catchmate.data.dto.enroll.PatchEnrollAcceptResponseDTO
import com.catchmate.data.dto.enroll.PatchEnrollRejectResponseDTO
import com.catchmate.data.dto.enroll.PostEnrollRequestDTO
import com.catchmate.data.dto.enroll.PostEnrollResponseDTO
import com.catchmate.data.dto.enroll.ReceivedEnrollInfoDTO
import com.catchmate.data.dto.enroll.ReceivedEnrollInfoResponseDTO
import com.catchmate.data.dto.enroll.UserInfoDTO
import com.catchmate.data.mapper.BoardMapper.toFavoriteClub
import com.catchmate.domain.model.enroll.AllReceivedEnrollInfoResponse
import com.catchmate.domain.model.enroll.DeleteEnrollResponse
import com.catchmate.domain.model.enroll.EnrollBoardInfo
import com.catchmate.domain.model.enroll.EnrollInfo
import com.catchmate.domain.model.enroll.GameInfo
import com.catchmate.domain.model.enroll.GetAllReceivedEnrollResponse
import com.catchmate.domain.model.enroll.GetEnrollNewCountResponse
import com.catchmate.domain.model.enroll.GetReceivedEnrollResponse
import com.catchmate.domain.model.enroll.GetRequestedEnrollListResponse
import com.catchmate.domain.model.enroll.GetRequestedEnrollResponse
import com.catchmate.domain.model.enroll.PatchEnrollAcceptResponse
import com.catchmate.domain.model.enroll.PatchEnrollRejectResponse
import com.catchmate.domain.model.enroll.PostEnrollRequest
import com.catchmate.domain.model.enroll.PostEnrollResponse
import com.catchmate.domain.model.enroll.ReceivedEnrollInfo
import com.catchmate.domain.model.enroll.ReceivedEnrollInfoResponse
import com.catchmate.domain.model.enroll.UserInfo

object EnrollMapper {
    fun toPostEnrollRequestDTO(request: PostEnrollRequest): PostEnrollRequestDTO =
        PostEnrollRequestDTO(
            description = request.description,
        )

    fun toPostEnrollResponse(responseDTO: PostEnrollResponseDTO): PostEnrollResponse =
        PostEnrollResponse(
            enrollId = responseDTO.enrollId,
            requestAt = responseDTO.requestAt,
        )

    fun toPatchEnrollRejectResponse(responseDTO: PatchEnrollRejectResponseDTO): PatchEnrollRejectResponse =
        PatchEnrollRejectResponse(
            enrollId = responseDTO.enrollId,
            acceptStatus = responseDTO.acceptStatus,
        )

    fun toPatchEnrollAcceptResponse(responseDTO: PatchEnrollAcceptResponseDTO): PatchEnrollAcceptResponse =
        PatchEnrollAcceptResponse(
            enrollId = responseDTO.enrollId,
            acceptStatus = responseDTO.acceptStatus,
        )

    fun toGetRequestedEnrollResponse(dto: GetRequestedEnrollResponseDTO): GetRequestedEnrollResponse =
        GetRequestedEnrollResponse(
            enrollId = dto.enrollId,
            description = dto.description,
        )

    fun toGetRequestedEnrollListResponse(responseDTO: GetRequestedEnrollListResponseDTO): GetRequestedEnrollListResponse =
        GetRequestedEnrollListResponse(
            enrollInfoList = responseDTO.enrollInfoList.map { toEnrollInfo(it) },
            totalPages = responseDTO.totalPages,
            totalElements = responseDTO.totalElements,
            isFirst = responseDTO.isFirst,
            isLast = responseDTO.isLast,
        )

    private fun toEnrollInfo(dto: EnrollInfoDTO): EnrollInfo =
        EnrollInfo(
            enrollId = dto.enrollId,
            acceptStatus = dto.acceptStatus,
            description = dto.description,
            requestDate = dto.requestDate,
            userInfo = toEnrollUserInfo(dto.userInfo),
            boardInfo = toEnrollBoardInfo(dto.boardInfo),
        )

    private fun toEnrollUserInfo(dto: UserInfoDTO): UserInfo =
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

    private fun toEnrollBoardInfo(dto: EnrollBoardInfoDTO): EnrollBoardInfo =
        EnrollBoardInfo(
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
            userInfo = toEnrollUserInfo(dto.userInfo),
            buttonStatus = dto.buttonStatus,
            chatRoomId = dto.chatRoomId,
            bookMarked = dto.bookMarked,
        )

    private fun toGameInfo(dto: GameInfoDTO): GameInfo =
        GameInfo(
            homeClubId = dto.homeClubId,
            awayClubId = dto.awayClubId,
            gameStartDate = dto.gameStartDate,
            location = dto.location,
        )

    fun toGetReceivedEnrollResponse(responseDTO: GetReceivedEnrollResponseDTO): GetReceivedEnrollResponse =
        GetReceivedEnrollResponse(
            enrollInfoList = responseDTO.enrollInfoList.map { toReceivedEnrollInfoResponse(it) },
            totalPages = responseDTO.totalPages,
            totalElements = responseDTO.totalElements,
            isFirst = responseDTO.isFirst,
            isLast = responseDTO.isLast,
        )

    private fun toReceivedEnrollInfoResponse(dto: ReceivedEnrollInfoResponseDTO): ReceivedEnrollInfoResponse =
        ReceivedEnrollInfoResponse(
            enrollReceiveInfoList = dto.enrollReceiveInfoList.map { toReceivedEnrollInfo(it) },
        )

    private fun toReceivedEnrollInfo(dto: ReceivedEnrollInfoDTO): ReceivedEnrollInfo =
        ReceivedEnrollInfo(
            enrollId = dto.enrollId,
            acceptStatus = dto.acceptStatus,
            description = dto.description,
            requestDate = dto.requestDate,
            userInfo = toEnrollUserInfo(dto.userInfo),
            new = dto.new,
        )

    fun toGetAllReceivedEnrollResponse(responseDTO: GetAllReceivedEnrollResponseDTO): GetAllReceivedEnrollResponse =
        GetAllReceivedEnrollResponse(
            enrollInfoList = responseDTO.enrollInfoList.map { toAllReceivedEnrollInfoResponse(it) },
            totalPages = responseDTO.totalPages,
            totalElements = responseDTO.totalElements,
            isFirst = responseDTO.isFirst,
            isLast = responseDTO.isLast,
        )

    private fun toAllReceivedEnrollInfoResponse(dto: AllReceivedEnrollInfoResponseDTO): AllReceivedEnrollInfoResponse =
        AllReceivedEnrollInfoResponse(
            boardInfo = toEnrollBoardInfo(dto.boardInfo),
            enrollReceiveInfoList = dto.enrollReceiveInfoList.map { toReceivedEnrollInfo(it) },
        )

    fun toGetEnrollNewCountResponse(responseDTO: GetEnrollNewCountResponseDTO): GetEnrollNewCountResponse =
        GetEnrollNewCountResponse(
            newEnrollCount = responseDTO.newEnrollCount,
        )

    fun toDeleteEnrollResponse(responseDTO: DeleteEnrollResponseDTO): DeleteEnrollResponse =
        DeleteEnrollResponse(
            enrollId = responseDTO.enrollId,
            deletedAt = responseDTO.deletedAt,
        )
}
