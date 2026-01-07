package com.catchmate.data.datasource.remote

import com.catchmate.data.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject

class AuthRetrofitClient
    @Inject
    constructor() {
        private val logging =
            HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }

        private val okHttpClient =
            OkHttpClient
                .Builder()
                .addInterceptor(logging)
                .build()

        val retrofit: AuthService =
            Retrofit
                .Builder()
                .baseUrl(BuildConfig.SERVER_DOMAIN)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(AuthService::class.java)
    }
