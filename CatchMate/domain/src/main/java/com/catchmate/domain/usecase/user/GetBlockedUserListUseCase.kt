package com.catchmate.domain.usecase.user

import com.catchmate.domain.model.user.GetBlockedUserListResponse
import com.catchmate.domain.repository.UserRepository
import javax.inject.Inject

class GetBlockedUserListUseCase
    @Inject
    constructor(
        private val userRepository: UserRepository,
    ) {
        suspend operator fun invoke(page: Int): Result<GetBlockedUserListResponse> = userRepository.getBlockedUserList(page)
    }
