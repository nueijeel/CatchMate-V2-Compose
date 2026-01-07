package com.catchmate.data.di

import com.catchmate.data.repository.LocalDataRepositoryImpl
import com.catchmate.domain.repository.LocalDataRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object LocalDataModule {
    @Provides
    fun provideLocalDataRepository(localDataRepositoryImpl: LocalDataRepositoryImpl): LocalDataRepository = localDataRepositoryImpl
}
