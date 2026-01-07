package com.catchmate.domain.usecase.enroll

import com.catchmate.domain.model.enroll.PatchEnrollRejectResponse
import com.catchmate.domain.repository.EnrollRepository
import javax.inject.Inject

class PatchEnrollRejectUseCase
    @Inject
    constructor(
        private val enrollRepository: EnrollRepository,
    ) {
        suspend fun patchEnrollReject(enrollId: Long): Result<PatchEnrollRejectResponse> = enrollRepository.patchEnrollReject(enrollId)
    }
