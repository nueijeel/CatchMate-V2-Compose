package com.catchmate.presentation.view.notification

import android.content.Context
import com.catchmate.domain.NotificationHandler
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object NotificationHandlerModule {
    @Provides
    fun provideNotificationHandler(
        @ApplicationContext context: Context,
    ): NotificationHandler = NotificationHandlerImpl(context)
}
