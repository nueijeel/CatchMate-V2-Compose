package com.catchmate.domain.model.board

import com.catchmate.domain.model.enroll.GameInfo
import com.catchmate.domain.model.enroll.UserInfo

data class PostBoardResponse(
    val boardId: Long,
    val title: String,
    val content: String,
    val cheerClubId: Int,
    val currentPerson: Int,
    val maxPerson: Int,
    val preferredGender: String,
    val preferredAgeRange: String,
    val gameInfo: GameInfo,
    val liftUpDate: String,
    val userInfo: UserInfo,
)
