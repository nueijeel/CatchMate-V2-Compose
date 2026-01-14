package com.catchmate.domain.usecase.auth

import android.app.Activity
import com.catchmate.domain.exception.Result
import com.catchmate.domain.repository.AuthRepository
import javax.inject.Inject

class SignInWithGoogleUseCase
    @Inject
    constructor(
        private val authRepository: AuthRepository,
    ) {
        suspend fun signInWithGoogle(activity: Activity): Result<Pair<String, String>> = authRepository.signInWithGoogle(activity)
    }
