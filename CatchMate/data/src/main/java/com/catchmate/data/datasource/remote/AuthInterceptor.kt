package com.catchmate.data.datasource.remote

import com.catchmate.data.datasource.local.LocalStorageDataSource
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor
    @Inject
    constructor(
        private val localStorageDataSource: LocalStorageDataSource,
    ) : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val accessToken =
                runBlocking {
                    localStorageDataSource.getAccessToken()
                }

            val request =
                chain
                    .request()
                    .newBuilder()
                    .header("AccessToken", accessToken)
                    .build()

            return chain.proceed(request)
        }
    }
