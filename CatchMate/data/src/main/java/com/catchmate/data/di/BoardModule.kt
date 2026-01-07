package com.catchmate.data.di

import com.catchmate.data.repository.BoardRepositoryImpl
import com.catchmate.domain.repository.BoardRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object BoardModule {
    @Provides
    fun provideBoardRepository(boardRepositoryImpl: BoardRepositoryImpl): BoardRepository = boardRepositoryImpl
}
