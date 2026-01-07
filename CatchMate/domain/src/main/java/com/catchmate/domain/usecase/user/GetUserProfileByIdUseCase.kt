package com.catchmate.domain.usecase.user

import com.catchmate.domain.model.user.GetUserProfileByIdResponse
import com.catchmate.domain.repository.UserRepository
import javax.inject.Inject

class GetUserProfileByIdUseCase
    @Inject
    constructor(
        private val userRepository: UserRepository,
    ) {
        suspend fun getUserProfileById(profileUserId: Long): Result<GetUserProfileByIdResponse> =
            userRepository.getUserProfileById(profileUserId)
    }
