package com.catchmate.data.repository

import android.util.Log
import com.catchmate.data.datasource.remote.AuthService
import com.catchmate.data.datasource.remote.RetrofitClient
import com.catchmate.data.mapper.AuthMapper
import com.catchmate.data.util.ApiResponseHandleUtil.apiCall
import com.catchmate.domain.model.auth.DeleteLogoutResponse
import com.catchmate.domain.model.auth.GetCheckNicknameResponse
import com.catchmate.domain.model.auth.PostLoginRequest
import com.catchmate.domain.model.auth.PostLoginResponse
import com.catchmate.domain.repository.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl
    @Inject
    constructor(
        retrofitClient: RetrofitClient,
    ) : AuthRepository {
        private val authApi = retrofitClient.createApi<AuthService>()
        private val tag = "AuthRepo"

        override suspend fun postAuthLogin(postLoginRequest: PostLoginRequest): PostLoginResponse? =
            try {
                val response = authApi.postAuthLogin(AuthMapper.toPostLoginRequestDTO(postLoginRequest))
                if (response.isSuccessful) {
                    Log.d("AuthRepository", "통신 성공 : ${response.code()}")
                    response.body()?.let { AuthMapper.toPostLoginResponse(it) } ?: throw Exception("Empty Response")
                } else {
                    Log.d("AuthRepository", "통신 실패 : ${response.code()}")
                    null
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }

        override suspend fun getAuthCheckNickname(nickName: String): Result<GetCheckNicknameResponse> =
            apiCall(
                tag = this.tag,
                apiFunction = { authApi.getAuthCheckNickname(nickName) },
                transform = { AuthMapper.toGetCheckNicknameResponse(it!!) },
            )

        override suspend fun deleteAuthLogout(refreshToken: String): Result<DeleteLogoutResponse> =
            apiCall(
                tag = this.tag,
                apiFunction = { authApi.deleteAuthLogout(refreshToken) },
                transform = { AuthMapper.toDeleteLogoutResponse(it!!) },
            )
    }
