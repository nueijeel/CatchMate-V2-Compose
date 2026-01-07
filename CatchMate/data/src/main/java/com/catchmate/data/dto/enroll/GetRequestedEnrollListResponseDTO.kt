package com.catchmate.data.dto.enroll

data class GetRequestedEnrollListResponseDTO(
    val enrollInfoList: List<EnrollInfoDTO>,
    val totalPages: Int,
    val totalElements: Int,
    val isFirst: Boolean,
    val isLast: Boolean,
)
