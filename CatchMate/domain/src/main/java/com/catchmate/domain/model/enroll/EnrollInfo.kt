package com.catchmate.domain.model.enroll

data class EnrollInfo(
    val enrollId: Long,
    val acceptStatus: String,
    val description: String,
    val requestDate: String,
    val userInfo: UserInfo,
    val boardInfo: EnrollBoardInfo,
)
