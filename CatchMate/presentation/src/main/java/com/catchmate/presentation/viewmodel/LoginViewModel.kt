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
import com.catchmate.domain.usecase.auth.SignInWithGoogleUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel
    @Inject
    constructor(
        private val signWithGoogleUseCase: SignInWithGoogleUseCase,
    ) : ViewModel() {
        private var _resultPair = MutableLiveData<Pair<String, String>>()
        val resultPair: LiveData<Pair<String, String>>
            get() = _resultPair

        fun signWithGoogle(activity: Activity) {
            viewModelScope.launch {
                val result = signWithGoogleUseCase.signInWithGoogle(activity)
                when (result) {
                    is Result.Success -> {
                        Log.d("login vm", "uid : ${result.data.first} / email : ${result.data.second}")
                        _resultPair.value = result.data
                    }

                    is Result.Error -> {
                        when (result.exception) {
                            is GoogleLoginException.Cancelled -> {
                                Log.e("GoogleLoginError", "로그인이 취소되었습니다.")
                            }

                            is GoogleLoginException.NoCredentials -> {
                                "앱 로그인을 위해서 기기에 Google 계정을 등록해주세요."
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
    }
