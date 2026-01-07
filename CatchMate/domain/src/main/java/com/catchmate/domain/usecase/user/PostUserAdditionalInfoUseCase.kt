package com.catchmate.domain.usecase.user

import com.catchmate.domain.model.user.PostUserAdditionalInfoRequest
import com.catchmate.domain.model.user.PostUserAdditionalInfoResponse
import com.catchmate.domain.repository.UserRepository
import javax.inject.Inject

class PostUserAdditionalInfoUseCase
    @Inject
    constructor(
        private val userRepository: UserRepository,
    ) {
        suspend fun postUserAdditionalInfo(
            postUserAdditionalInfoRequest: PostUserAdditionalInfoRequest,
        ): Result<PostUserAdditionalInfoResponse> = userRepository.postUserAdditionalInfo(postUserAdditionalInfoRequest)
    }
