package com.catchmate.domain.usecase.local

import com.catchmate.domain.repository.LocalDataRepository
import javax.inject.Inject

class LocalDataUseCase
    @Inject
    constructor(
        private val localDataRepository: LocalDataRepository,
    ) {
        fun saveAccessToken(accessToken: String) {
            localDataRepository.saveAccessToken(accessToken)
        }

        fun saveRefreshToken(refreshToken: String) {
            localDataRepository.saveRefreshToken(refreshToken)
        }

        fun saveUserId(userId: Long) {
            localDataRepository.saveUserId(userId)
        }

        fun saveProvider(provider: String) {
            localDataRepository.saveProvider(provider)
        }

        fun getAccessToken(): String = localDataRepository.getAccessToken()

        fun getRefreshToken(): String = localDataRepository.getRefreshToken()

        fun getUserId(): Long = localDataRepository.getUserId()

        fun getProvider(): String = localDataRepository.getProvider()

        fun removeTokens() {
            localDataRepository.removeTokens()
        }

        fun removeUserId() {
            localDataRepository.removeUserId()
        }

        fun removeProvider() {
            localDataRepository.removeProvider()
        }
    }
