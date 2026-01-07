package com.catchmate.domain.usecase.user

import com.catchmate.domain.model.user.DeleteUserAccountResponse
import com.catchmate.domain.repository.UserRepository
import javax.inject.Inject

class DeleteUserAccountUseCase
    @Inject
    constructor(
        private val userRepository: UserRepository,
    ) {
        suspend operator fun invoke(): Result<DeleteUserAccountResponse> = userRepository.deleteUserAccount()
    }
