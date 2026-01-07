package com.catchmate.data.dto.enroll

data class ReceivedEnrollInfoDTO(
    val enrollId: Long,
    val acceptStatus: String,
    val description: String,
    val requestDate: String,
    val userInfo: UserInfoDTO,
    val new: Boolean,
)
