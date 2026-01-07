package com.catchmate.domain.usecase.auth

import android.app.Activity
import com.catchmate.domain.exception.Result
import com.catchmate.domain.model.auth.PostLoginRequest
import com.catchmate.domain.repository.LoginRepository
import javax.inject.Inject

class SocialLoginUseCase
    @Inject
    constructor(
        private val loginRepository: LoginRepository,
    ) {
        suspend fun loginWithKakao(): PostLoginRequest? = loginRepository.loginWithKakao()

        suspend fun loginWithNaver(activity: Activity): PostLoginRequest? = loginRepository.loginWithNaver(activity)

        suspend fun loginWithGoogle(activity: Activity): Result<PostLoginRequest> = loginRepository.loginWithGoogle(activity)
    }
