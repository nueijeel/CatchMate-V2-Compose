package com.catchmate.data.dto.enroll

data class EnrollInfoDTO(
    val enrollId: Long,
    val acceptStatus: String,
    val description: String,
    val requestDate: String,
    val userInfo: UserInfoDTO,
    val boardInfo: EnrollBoardInfoDTO,
)
