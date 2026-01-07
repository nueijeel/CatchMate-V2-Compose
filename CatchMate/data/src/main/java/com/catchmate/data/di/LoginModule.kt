package com.catchmate.data.di

import com.catchmate.data.repository.LoginRepositoryImpl
import com.catchmate.domain.repository.LoginRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object LoginModule {
    @Provides
    fun provideLoginRepository(loginRepositoryImpl: LoginRepositoryImpl): LoginRepository = loginRepositoryImpl
}
