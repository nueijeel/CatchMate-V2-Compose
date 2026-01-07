package com.catchmate.domain.usecase.enroll

import com.catchmate.domain.model.enroll.GetRequestedEnrollListResponse
import com.catchmate.domain.repository.EnrollRepository
import javax.inject.Inject

class GetRequestedEnrollListUseCase
    @Inject
    constructor(
        private val enrollRepository: EnrollRepository,
    ) {
        suspend fun getRequestedEnrollList(page: Int): Result<GetRequestedEnrollListResponse> =
            enrollRepository.getRequestedEnrollList(page)
    }
