package com.catchmate.data.dto.board

import com.catchmate.data.dto.enroll.GameInfoDTO

data class PatchBoardRequestDTO(
    val title: String,
    val content: String,
    val maxPerson: Int,
    val cheerClubId: Int,
    val preferredGender: String,
    val preferredAgeRange: List<String>,
    val gameRequest: GameInfoDTO,
    val isCompleted: Boolean,
)
