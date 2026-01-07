package com.catchmate.domain.usecase.user

import com.catchmate.domain.model.user.GetUserProfileResponse
import com.catchmate.domain.repository.UserRepository
import javax.inject.Inject

class GetUserProfileUseCase
    @Inject
    constructor(
        private val userRepository: UserRepository,
    ) {
        suspend fun getUserProfile(): Result<GetUserProfileResponse> = userRepository.getUserProfile()
    }
