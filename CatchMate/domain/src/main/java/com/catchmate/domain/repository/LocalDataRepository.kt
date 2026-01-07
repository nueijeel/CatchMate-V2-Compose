package com.catchmate.domain.repository

interface LocalDataRepository {
    fun saveAccessToken(accessToken: String)

    fun saveRefreshToken(refreshToken: String)

    fun saveUserId(userId: Long)

    fun saveProvider(provider: String)

    fun getAccessToken(): String

    fun getRefreshToken(): String

    fun getUserId(): Long

    fun getProvider(): String

    fun removeTokens()

    fun removeUserId()

    fun removeProvider()
}
