package com.catchmate.data.dto.board

import com.catchmate.data.dto.enroll.GameInfoDTO

data class PostBoardRequestDTO(
    val title: String,
    val content: String,
    val maxPerson: Int,
    val cheerClubId: Int,
    val preferredGender: String, // 미 선택 시 빈 문자열
    val preferredAgeRange: List<String>, // 미 선택 시 빈 배열
    val gameRequest: GameInfoDTO,
    val isCompleted: Boolean, // 임시 저장 시 false, 게시글 작성 시 true
)
