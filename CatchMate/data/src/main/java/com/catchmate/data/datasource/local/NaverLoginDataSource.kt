package com.catchmate.data.datasource.local

import android.app.Activity
import android.util.Log
import com.catchmate.data.datasource.remote.FCMTokenService
import com.catchmate.data.dto.auth.PostLoginRequestDTO
import com.catchmate.domain.model.enumclass.LoginPlatform
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.NidOAuthLogin
import com.navercorp.nid.oauth.OAuthLoginCallback
import com.navercorp.nid.profile.NidProfileCallback
import com.navercorp.nid.profile.data.NidProfileResponse
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class NaverLoginDataSource
    @Inject
    constructor(
        private val fcmTokenService: FCMTokenService,
    ) {
        suspend fun loginWithNaver(activity: Activity): PostLoginRequestDTO =
            suspendCancellableCoroutine { continuation ->
                val nidProfileCallback =
                    object : NidProfileCallback<NidProfileResponse> {
                        override fun onError(
                            errorCode: Int,
                            message: String,
                        ) {
                            onFailure(errorCode, message)
                            continuation.resumeWithException(Exception(message))
                        }

                        override fun onFailure(
                            httpStatus: Int,
                            message: String,
                        ) {
                            loginFail()
                            continuation.resumeWithException(Exception(message))
                        }

                        override fun onSuccess(result: NidProfileResponse) {
                            if (result.profile != null) {
                                Log.i("NaverInfoSuccess", "providerId : ${result.profile?.id} email : ${result.profile?.email}")
                                result.profile?.let {
                                    val postLoginRequestDTO =
                                        PostLoginRequestDTO(
                                            email = it.email!!,
                                            providerId = it.id!!,
                                            provider = LoginPlatform.NAVER.toString().lowercase(),
                                            picture = it.profileImage!!,
                                            fcmToken =
                                                runBlocking {
                                                    fcmTokenService.getToken()
                                                },
                                        )
                                    continuation.resume(postLoginRequestDTO)
                                } ?: continuation.resumeWithException(Exception("Profile is null"))
                            }
                        }
                    }

                val oAuthLoginCallback =
                    object : OAuthLoginCallback {
                        override fun onError(
                            errorCode: Int,
                            message: String,
                        ) {
                            onFailure(errorCode, message)
                        }

                        override fun onFailure(
                            httpStatus: Int,
                            message: String,
                        ) {
                            loginFail()
                        }

                        override fun onSuccess() {
                            Log.d("NaverLoginSuccess", "네이버 로그인 성공")
                            NidOAuthLogin().callProfileApi(nidProfileCallback)
                        }
                    }

                NaverIdLoginSDK.authenticate(activity, oAuthLoginCallback)
            }

        private fun loginFail() {
            val errorCode = NaverIdLoginSDK.getLastErrorCode().code
            val errorDescription = NaverIdLoginSDK.getLastErrorDescription()
            Log.i("NaverLoginFail", "errorCode:$errorCode errorDescription:$errorDescription")
        }
    }
