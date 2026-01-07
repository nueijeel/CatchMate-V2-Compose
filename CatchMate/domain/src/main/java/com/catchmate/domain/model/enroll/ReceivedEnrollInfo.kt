package com.catchmate.domain.model.enroll

data class ReceivedEnrollInfo(
    val enrollId: Long,
    val acceptStatus: String,
    val description: String,
    val requestDate: String,
    val userInfo: UserInfo,
    val new: Boolean,
)
