package com.catchmate.data.di

import com.catchmate.data.repository.SupportRepositoryImpl
import com.catchmate.domain.repository.SupportRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object SupportModule {
    @Provides
    fun provideSupportRepository(supportRepositoryImpl: SupportRepositoryImpl): SupportRepository = supportRepositoryImpl
}
