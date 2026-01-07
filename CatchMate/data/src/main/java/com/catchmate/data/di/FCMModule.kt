package com.catchmate.data.di

import com.catchmate.data.datasource.remote.FCMTokenService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object FCMModule {
    @Provides
    fun provideFCMTokenService(): FCMTokenService = FCMTokenService()
}
