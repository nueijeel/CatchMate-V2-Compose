package com.catchmate.domain

import androidx.core.app.NotificationCompat

interface NotificationHandler {
    fun createNotificationBuilder(
        data: Map<String, String>,
        title: String,
        body: String,
        notificationBuilder: NotificationCompat.Builder,
    ): NotificationCompat.Builder
}
