package com.catchmate.domain.usecase.notification

import com.catchmate.domain.model.notification.DeleteReceivedNotificationResponse
import com.catchmate.domain.repository.NotificationRepository
import javax.inject.Inject

class DeleteReceivedNotificationUseCase
    @Inject
    constructor(
        private val notificationRepository: NotificationRepository,
    ) {
        suspend fun deleteReceivedNotification(notificationId: Long): Result<DeleteReceivedNotificationResponse> =
            notificationRepository.deleteReceivedNotification(notificationId)
    }
