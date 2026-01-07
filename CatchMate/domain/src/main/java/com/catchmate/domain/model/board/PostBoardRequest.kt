package com.catchmate.domain.model.board

import com.catchmate.domain.model.enroll.GameInfo

data class PostBoardRequest(
    val title: String,
    val content: String,
    val maxPerson: Int,
    val cheerClubId: Int,
    val preferredGender: String,
    val preferredAgeRange: List<String>,
    val gameRequest: GameInfo,
    val isCompleted: Boolean,
)
