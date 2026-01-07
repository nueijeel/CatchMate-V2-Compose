package com.catchmate.data.datasource.remote

import com.catchmate.data.datasource.local.LocalStorageDataSource
import com.catchmate.domain.exception.ReissueFailureException
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject

class AuthAuthenticator
    @Inject
    constructor(
        private val localStorageDataSource: LocalStorageDataSource,
        private val authRetrofitClient: AuthRetrofitClient,
    ) : Authenticator {
        override fun authenticate(
            route: Route?,
            response: Response,
        ): Request? {
            val refreshToken =
                runBlocking {
                    localStorageDataSource.getRefreshToken()
                }

            val reissueResponse =
                runBlocking {
                    authRetrofitClient.retrofit.postAuthReissue(refreshToken)
                }

            return if (reissueResponse.isSuccessful) {
                val newAccessToken = reissueResponse.body()?.accessToken

                if (newAccessToken != null) {
                    localStorageDataSource.saveAccessToken(newAccessToken)
                    response.request
                        .newBuilder()
                        .header("AccessToken", newAccessToken)
                        .build()
                } else {
                    throw ReissueFailureException("Reissue Error - AccessToken is null")
                }
            } else {
                throw ReissueFailureException("Reissue Error - Reissue Failure")
            }
        }
    }
