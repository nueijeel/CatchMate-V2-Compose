package com.catchmate.data.dto.enroll

data class GameInfoDTO(
    val homeClubId: Int,
    val awayClubId: Int,
    val gameStartDate: String?,
    val location: String,
)
