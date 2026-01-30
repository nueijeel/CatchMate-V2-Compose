package com.catchmate.domain.usecase.auth

import com.catchmate.domain.model.auth.UserEntity
import com.catchmate.domain.repository.UserRepository
import javax.inject.Inject

class SaveUserDataUseCase
@Inject
constructor(
    private val userRepository: UserRepository,
) {
    suspend operator fun invoke(uid: String, user: UserEntity): Result<Unit> = userRepository.saveUser(uid, user)
}
