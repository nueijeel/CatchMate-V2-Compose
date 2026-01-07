package com.catchmate.domain.repository

import android.app.Activity
import com.catchmate.domain.exception.Result
import com.catchmate.domain.model.auth.PostLoginRequest

interface LoginRepository {
    suspend fun loginWithKakao(): PostLoginRequest?

    suspend fun loginWithNaver(activity: Activity): PostLoginRequest?

    suspend fun loginWithGoogle(activity: Activity): Result<PostLoginRequest>
}
