package com.catchmate.data.repository

import android.app.Activity
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.NoCredentialException
import com.catchmate.data.datasource.local.GoogleLoginDataSource
import com.catchmate.data.datasource.local.KakaoLoginDataSource
import com.catchmate.data.datasource.local.NaverLoginDataSource
import com.catchmate.data.mapper.AuthMapper.toGooglePostLoginRequest
import com.catchmate.data.mapper.AuthMapper.toPostLoginRequest
import com.catchmate.domain.exception.GoogleLoginException
import com.catchmate.domain.exception.Result
import com.catchmate.domain.model.auth.PostLoginRequest
import com.catchmate.domain.repository.LoginRepository
import javax.inject.Inject

class LoginRepositoryImpl
    @Inject
    constructor(
        private val kakaoLoginDataSource: KakaoLoginDataSource,
        private val naverLoginDataSource: NaverLoginDataSource,
        private val googleLoginDataSource: GoogleLoginDataSource,
    ) : LoginRepository {
        override suspend fun loginWithKakao(): PostLoginRequest? {
            val postLoginRequestDTO = kakaoLoginDataSource.loginWithKakao()
            return toPostLoginRequest(postLoginRequestDTO)
        }

        override suspend fun loginWithNaver(activity: Activity): PostLoginRequest? {
            val postLoginRequestDTO = naverLoginDataSource.loginWithNaver(activity)
            return toPostLoginRequest(postLoginRequestDTO)
        }

        override suspend fun loginWithGoogle(activity: Activity): Result<PostLoginRequest> =
            try {
                val credentialResult = googleLoginDataSource.getCredential(activity)

                when (credentialResult) {
                    is Result.Success -> {
                        val signInResult = googleLoginDataSource.handleSignIn(credentialResult.data)

                        when (signInResult) {
                            is Result.Success -> {
                                val loginDTO = signInResult.data
                                Result.Success(
                                    toGooglePostLoginRequest(loginDTO),
                                )
                            }

                            is Result.Error -> {
                                when (signInResult.exception) {
                                    is GoogleLoginException.TokenParsing -> Result.Error(exception = GoogleLoginException.TokenParsing)
                                    else -> Result.Error(exception = GoogleLoginException.Unknown(signInResult.exception!!))
                                }
                            }
                        }
                    }

                    is Result.Error -> {
                        when (credentialResult.exception) {
                            is GoogleLoginException.Cancelled,
                            is GetCredentialCancellationException,
                            -> {
                                Result.Error(exception = GoogleLoginException.Cancelled)
                            }

                            is GoogleLoginException.NoCredentials,
                            is NoCredentialException,
                            -> {
                                Result.Error(exception = GoogleLoginException.NoCredentials)
                            }

                            else -> {
                                Result.Error(
                                    exception =
                                        GoogleLoginException.Unknown(
                                            credentialResult.exception!!,
                                        ),
                                )
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                Result.Error(exception = GoogleLoginException.Unknown(e))
            }
    }
