package com.catchmate.domain.usecase.enroll

import com.catchmate.domain.model.enroll.PatchEnrollAcceptResponse
import com.catchmate.domain.repository.EnrollRepository
import javax.inject.Inject

class PatchEnrollAcceptUseCase
    @Inject
    constructor(
        private val enrollRepository: EnrollRepository,
    ) {
        suspend fun patchEnrollAccept(enrollId: Long): Result<PatchEnrollAcceptResponse> = enrollRepository.patchEnrollAccept(enrollId)
    }
