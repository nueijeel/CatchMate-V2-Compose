package com.catchmate.domain.usecase.auth

import com.catchmate.domain.model.auth.GetCheckNicknameResponse
import com.catchmate.domain.repository.AuthRepository
import javax.inject.Inject

class GetAuthCheckNicknameUseCase
    @Inject
    constructor(
        private val authRepository: AuthRepository,
    ) {
        suspend fun getAuthCheckNickname(nickName: String): Result<GetCheckNicknameResponse> = authRepository.getAuthCheckNickname(nickName)
    }
