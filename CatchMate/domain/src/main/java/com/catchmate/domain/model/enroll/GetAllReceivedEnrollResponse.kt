package com.catchmate.domain.model.enroll

data class GetAllReceivedEnrollResponse(
    val enrollInfoList: List<AllReceivedEnrollInfoResponse>,
    val totalPages: Int,
    val totalElements: Int,
    val isFirst: Boolean,
    val isLast: Boolean,
)
