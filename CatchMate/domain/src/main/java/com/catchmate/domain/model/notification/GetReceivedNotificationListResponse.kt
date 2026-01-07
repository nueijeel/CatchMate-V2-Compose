package com.catchmate.domain.model.notification

data class GetReceivedNotificationListResponse(
    val notificationInfoList: List<NotificationInfo>,
    val totalPages: Int,
    val totalElements: Int,
    val isFirst: Boolean,
    val isLast: Boolean,
)
