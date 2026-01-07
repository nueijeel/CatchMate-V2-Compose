package com.catchmate.domain.model.enroll

data class GetRequestedEnrollListResponse(
    val enrollInfoList: List<EnrollInfo>,
    val totalPages: Int,
    val totalElements: Int,
    val isFirst: Boolean,
    val isLast: Boolean,
)
