package com.catchmate.domain.model.enroll

data class GetReceivedEnrollResponse(
    val enrollInfoList: List<ReceivedEnrollInfoResponse>,
    val totalPages: Int,
    val totalElements: Int,
    val isFirst: Boolean,
    val isLast: Boolean,
)
