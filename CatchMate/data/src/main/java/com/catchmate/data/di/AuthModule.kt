package com.catchmate.data.di

import com.catchmate.data.datasource.local.LocalStorageDataSource
import com.catchmate.data.datasource.remote.AuthAuthenticator
import com.catchmate.data.datasource.remote.AuthRetrofitClient
import com.catchmate.data.repository.AuthRepositoryImpl
import com.catchmate.domain.repository.AuthRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object AuthModule {
    @Provides
    fun provideAuthRepository(authRepositoryImpl: AuthRepositoryImpl): AuthRepository = authRepositoryImpl

    @Provides
    fun provideAuthRetrofitClient(): AuthRetrofitClient = AuthRetrofitClient()

    @Provides
    fun provideAuthAuthenticator(
        localStorageDataSource: LocalStorageDataSource,
        authRetrofitClient: AuthRetrofitClient,
    ): AuthAuthenticator = AuthAuthenticator(localStorageDataSource, authRetrofitClient)
}
