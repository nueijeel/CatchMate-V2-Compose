package com.catchmate.data.di

import com.catchmate.data.repository.NotificationRepositoryImpl
import com.catchmate.domain.repository.NotificationRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object NotificationModule {
    @Provides
    fun provideNotificationRepository(notificationRepositoryImpl: NotificationRepositoryImpl): NotificationRepository =
        notificationRepositoryImpl
}
