package com.catchmate.data.datasource.local

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class LocalStorageDataSource
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
    ) {
        private val fileName = "encrypted_preferences"

        private val sharedPreferences: SharedPreferences by lazy {
            val masterKey =
                MasterKey
                    .Builder(context, MasterKey.DEFAULT_MASTER_KEY_ALIAS)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build()

            EncryptedSharedPreferences.create(
                context,
                fileName,
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
            )
        }

        fun saveAccessToken(accessToken: String) {
            with(sharedPreferences.edit()) {
                putString("accessToken", accessToken)
                commit()
            }
        }

        fun saveRefreshToken(refreshToken: String) {
            with(sharedPreferences.edit()) {
                putString("refreshToken", refreshToken)
                commit()
            }
        }

        fun saveUserId(userId: Long) {
            with(sharedPreferences.edit()) {
                putLong("userId", userId)
                commit()
            }
        }

        fun saveProvider(provider: String) {
            with(sharedPreferences.edit()) {
                putString("provider", provider)
                commit()
            }
        }

        fun getAccessToken(): String = sharedPreferences.getString("accessToken", "") ?: ""

        fun getRefreshToken(): String = sharedPreferences.getString("refreshToken", "") ?: ""

        fun getUserId(): Long = sharedPreferences.getLong("userId", -1L)

        fun getProvider(): String = sharedPreferences.getString("provider", "") ?: ""

        fun removeTokens() {
            with(sharedPreferences.edit()) {
                remove("accessToken")
                remove("refreshToken")
                apply()
            }
        }

        fun removeUserId() {
            with(sharedPreferences.edit()) {
                remove("userId")
                apply()
            }
        }

        fun removeProvider() {
            with(sharedPreferences.edit()) {
                remove("provider")
                apply()
            }
        }
    }
