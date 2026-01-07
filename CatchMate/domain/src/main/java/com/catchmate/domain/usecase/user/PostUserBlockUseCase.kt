package com.catchmate.domain.usecase.user

import com.catchmate.domain.model.user.PostUserBlockResponse
import com.catchmate.domain.repository.UserRepository
import javax.inject.Inject

class PostUserBlockUseCase
    @Inject
    constructor(
        private val userRepository: UserRepository,
    ) {
        suspend operator fun invoke(blockedUserId: Long): Result<PostUserBlockResponse> = userRepository.postUserBlock(blockedUserId)
    }
