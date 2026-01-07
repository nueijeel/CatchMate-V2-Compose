package com.catchmate.domain.usecase.enroll

import com.catchmate.domain.model.enroll.DeleteEnrollResponse
import com.catchmate.domain.repository.EnrollRepository
import javax.inject.Inject

class DeleteEnrollUseCase
    @Inject
    constructor(
        private val enrollRepository: EnrollRepository,
    ) {
        suspend fun deleteEnroll(enrollId: Long): Result<DeleteEnrollResponse> = enrollRepository.deleteEnroll(enrollId)
    }
