package com.catchmate.data.dto.enroll

data class GetAllReceivedEnrollResponseDTO(
    val enrollInfoList: List<AllReceivedEnrollInfoResponseDTO>,
    val totalPages: Int,
    val totalElements: Int,
    val isFirst: Boolean,
    val isLast: Boolean,
)
