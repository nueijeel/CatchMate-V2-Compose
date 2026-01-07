package com.catchmate.presentation.viewmodel

import android.app.Activity
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.catchmate.domain.exception.GoogleLoginException
import com.catchmate.domain.exception.Result
import com.catchmate.domain.model.auth.PostLoginRequest
import com.catchmate.domain.model.auth.PostLoginResponse
import com.catchmate.domain.usecase.auth.PostAuthLoginUseCase
import com.catchmate.domain.usecase.auth.SocialLoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel
    @Inject
    constructor(
        private val socialLoginUseCase: SocialLoginUseCase,
        private val postAuthLoginUseCase: PostAuthLoginUseCase,
    ) : ViewModel() {
        private val _postLoginRequest = MutableLiveData<PostLoginRequest?>()
        val postLoginRequest: LiveData<PostLoginRequest?>
            get() = _postLoginRequest

        private val _postLoginResponse = MutableLiveData<PostLoginResponse?>()
        val postLoginResponse: LiveData<PostLoginResponse?>
            get() = _postLoginResponse

        private val _noCredentialException = MutableLiveData<String>()
        val noCredentialException: LiveData<String>
            get() = _noCredentialException

        fun initPostLoginRequest() {
            _postLoginRequest.value = null
        }

        fun initPostLoginResponse() {
            _postLoginResponse.value = null
        }

        fun kakaoLogin() {
            viewModelScope.launch {
                _postLoginRequest.value = socialLoginUseCase.loginWithKakao()
            }
        }

        fun naverLogin(activity: Activity) {
            viewModelScope.launch {
                _postLoginRequest.value = socialLoginUseCase.loginWithNaver(activity)
            }
        }

        fun googleLogin(activity: Activity) {
            viewModelScope.launch {
                val result = socialLoginUseCase.loginWithGoogle(activity)
                when (result) {
                    is Result.Success -> {
                        _postLoginRequest.value = result.data
                    }

                    is Result.Error -> {
                        when (result.exception) {
                            is GoogleLoginException.Cancelled -> {
                                Log.e("GoogleLoginError", "로그인이 취소되었습니다.")
                            }

                            is GoogleLoginException.NoCredentials -> {
                                _noCredentialException.value = "앱 로그인을 위해서 기기에 Google 계정을 등록해주세요."
                            }

                            is GoogleLoginException.TokenParsing -> {
                                Log.e("GoogleLoginError", "로그인 정보 처리 중 오류가 발생했습니다.")
                            }

                            is GoogleLoginException.Unknown -> {
                                Log.e("GoogleLoginError", "알 수 없는 오류가 발생했습니다.")
                            }

                            else -> {
                                Log.e("GoogleLoginError", "로그인 중 오류가 발생했습니다.")
                            }
                        }
                    }
                }
            }
        }

        fun postAuthLogin(postLoginRequest: PostLoginRequest) {
            viewModelScope.launch {
                _postLoginResponse.value = postAuthLoginUseCase.postAuthLogin(postLoginRequest)
            }
        }
    }
