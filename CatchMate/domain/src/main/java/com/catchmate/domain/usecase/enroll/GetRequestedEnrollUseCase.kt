package com.catchmate.domain.usecase.enroll

import com.catchmate.domain.model.enroll.GetRequestedEnrollResponse
import com.catchmate.domain.repository.EnrollRepository
import javax.inject.Inject

class GetRequestedEnrollUseCase
    @Inject
    constructor(
        private val enrollRepository: EnrollRepository,
    ) {
        suspend operator fun invoke(boardId: Long): Result<GetRequestedEnrollResponse> = enrollRepository.getRequestedEnroll(boardId)
    }
