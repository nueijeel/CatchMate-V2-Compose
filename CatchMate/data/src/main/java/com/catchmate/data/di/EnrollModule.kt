package com.catchmate.data.di

import com.catchmate.data.repository.EnrollRepositoryImpl
import com.catchmate.domain.repository.EnrollRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object EnrollModule {
    @Provides
    fun provideEnrollRepository(enrollRepositoryImpl: EnrollRepositoryImpl): EnrollRepository = enrollRepositoryImpl
}
