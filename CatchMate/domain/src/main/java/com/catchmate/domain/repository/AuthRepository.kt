package com.catchmate.domain.repository

import com.catchmate.domain.model.auth.DeleteLogoutResponse
import com.catchmate.domain.model.auth.GetCheckNicknameResponse
import com.catchmate.domain.model.auth.PostLoginRequest
import com.catchmate.domain.model.auth.PostLoginResponse

interface AuthRepository {
    suspend fun postAuthLogin(postLoginRequest: PostLoginRequest): PostLoginResponse?

    suspend fun getAuthCheckNickname(nickName: String): Result<GetCheckNicknameResponse>

    suspend fun deleteAuthLogout(refreshToken: String): Result<DeleteLogoutResponse>
}
