package com.catchmate.data.dto.board

import com.catchmate.data.dto.enroll.GameInfoDTO
import com.catchmate.data.dto.enroll.UserInfoDTO

data class BoardDTO(
    val boardId: Long,
    val title: String,
    val content: String,
    val cheerClubId: Int,
    val currentPerson: Int,
    val maxPerson: Int,
    val preferredGender: String,
    val preferredAgeRange: String,
    val gameInfo: GameInfoDTO,
    val liftUpDate: String,
    val userInfo: UserInfoDTO,
    val buttonStatus: String?,
    val chatRoomId: Long,
    val bookMarked: Boolean,
)
