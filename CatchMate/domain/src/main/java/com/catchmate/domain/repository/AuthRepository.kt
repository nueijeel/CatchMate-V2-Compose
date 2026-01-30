package com.catchmate.domain.repository

import android.app.Activity
import com.catchmate.domain.exception.Result

interface AuthRepository {
    suspend fun signInWithGoogle(activity: Activity): Result<Triple<String, String, Boolean>>
}
