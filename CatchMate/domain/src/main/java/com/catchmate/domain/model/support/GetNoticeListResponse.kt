package com.catchmate.domain.model.support

data class GetNoticeListResponse(
    val noticeInfoList: List<NoticeInfo>,
    val totalPages: Int,
    val totalElements: Int,
    val isFirst: Boolean,
    val isLast: Boolean,
)
