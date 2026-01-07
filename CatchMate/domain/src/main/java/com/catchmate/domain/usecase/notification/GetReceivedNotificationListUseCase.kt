package com.catchmate.domain.usecase.notification

import com.catchmate.domain.model.notification.GetReceivedNotificationListResponse
import com.catchmate.domain.repository.NotificationRepository
import javax.inject.Inject

class GetReceivedNotificationListUseCase
    @Inject
    constructor(
        private val notificationRepository: NotificationRepository,
    ) {
        suspend fun getReceivedNotificationList(page: Int): Result<GetReceivedNotificationListResponse> =
            notificationRepository.getReceivedNotificationList(page)
    }
