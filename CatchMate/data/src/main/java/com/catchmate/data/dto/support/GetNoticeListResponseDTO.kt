package com.catchmate.data.dto.support

data class GetNoticeListResponseDTO(
    val noticeInfoList: List<NoticeInfoDTO>,
    val totalPages: Int,
    val totalElements: Int,
    val isFirst: Boolean,
    val isLast: Boolean,
)
