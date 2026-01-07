package com.catchmate.domain.usecase.enroll

import com.catchmate.domain.model.enroll.GetEnrollNewCountResponse
import com.catchmate.domain.repository.EnrollRepository
import javax.inject.Inject

class GetEnrollNewCountUseCase
    @Inject
    constructor(
        private val enrollRepository: EnrollRepository,
    ) {
        suspend fun getEnrollNewCount(): Result<GetEnrollNewCountResponse> = enrollRepository.getEnrollNewCount()
    }
