package com.catchmate.domain.usecase.user

import com.catchmate.domain.model.user.PatchUserProfileResponse
import com.catchmate.domain.repository.UserRepository
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

class PatchUserProfileUseCase
    @Inject
    constructor(
        private val userRepository: UserRepository,
    ) {
        suspend fun patchUserProfile(
            request: RequestBody,
            profileImage: MultipartBody.Part,
        ): Result<PatchUserProfileResponse> = userRepository.patchUserProfile(request, profileImage)
    }
