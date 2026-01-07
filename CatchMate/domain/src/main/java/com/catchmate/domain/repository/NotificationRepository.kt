package com.catchmate.domain.repository

import com.catchmate.domain.model.notification.DeleteReceivedNotificationResponse
import com.catchmate.domain.model.notification.GetReceivedNotificationListResponse
import com.catchmate.domain.model.notification.GetReceivedNotificationResponse

interface NotificationRepository {
    // 받은 알림 목록 get
    suspend fun getReceivedNotificationList(page: Int): Result<GetReceivedNotificationListResponse>

    // 알림 상세 get
    suspend fun getReceivedNotification(notificationId: Long): Result<GetReceivedNotificationResponse>

    suspend fun deleteReceivedNotification(notificationId: Long): Result<DeleteReceivedNotificationResponse>
}
