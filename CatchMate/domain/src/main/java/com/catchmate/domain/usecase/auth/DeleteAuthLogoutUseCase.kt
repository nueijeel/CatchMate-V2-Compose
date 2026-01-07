package com.catchmate.domain.usecase.auth

import com.catchmate.domain.model.auth.DeleteLogoutResponse
import com.catchmate.domain.repository.AuthRepository
import javax.inject.Inject

class DeleteAuthLogoutUseCase
    @Inject
    constructor(
        private val authRepository: AuthRepository,
    ) {
        suspend fun deleteAuthLogout(refreshToken: String): Result<DeleteLogoutResponse> = authRepository.deleteAuthLogout(refreshToken)
    }
