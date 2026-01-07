package com.catchmate.domain.usecase.user

import com.catchmate.domain.model.user.GetUnreadInfoResponse
import com.catchmate.domain.repository.UserRepository
import javax.inject.Inject

class GetUnreadInfoUseCase
    @Inject
    constructor(
        private val userRepository: UserRepository,
    ) {
        suspend operator fun invoke(): Result<GetUnreadInfoResponse> = userRepository.getUnreadInfo()
    }
