package com.catchmate.data.di

import com.catchmate.data.repository.ChattingRepositoryImpl
import com.catchmate.domain.repository.ChattingRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object ChattingModule {
    @Provides
    fun provideChattingRepository(chattingRepositoryImpl: ChattingRepositoryImpl): ChattingRepository = chattingRepositoryImpl
}
