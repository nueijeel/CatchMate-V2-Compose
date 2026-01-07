package com.catchmate.data.dto.notification

data class GetReceivedNotificationListResponseDTO(
    val notificationInfoList: List<NotificationInfoDTO>,
    val totalPages: Int,
    val totalElements: Int,
    val isFirst: Boolean,
    val isLast: Boolean,
)
