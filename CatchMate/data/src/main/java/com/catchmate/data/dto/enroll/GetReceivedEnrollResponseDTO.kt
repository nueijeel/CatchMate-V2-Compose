package com.catchmate.data.dto.enroll

data class GetReceivedEnrollResponseDTO(
    val enrollInfoList: List<ReceivedEnrollInfoResponseDTO>,
    val totalPages: Int,
    val totalElements: Int,
    val isFirst: Boolean,
    val isLast: Boolean,
)
