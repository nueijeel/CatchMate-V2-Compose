package com.catchmate.domain.usecase.user

import com.catchmate.domain.model.user.DeleteBlockedUserResponse
import com.catchmate.domain.repository.UserRepository
import javax.inject.Inject

class DeleteBlockedUserUseCase
    @Inject
    constructor(
        private val userRepository: UserRepository,
    ) {
        suspend operator fun invoke(blockedUserId: Long): Result<DeleteBlockedUserResponse> =
            userRepository.deleteBlockedUser(blockedUserId)
    }
