package com.catchmate.domain.usecase.support

import com.catchmate.domain.model.support.PostUserReportRequest
import com.catchmate.domain.model.support.PostUserReportResponse
import com.catchmate.domain.repository.SupportRepository
import javax.inject.Inject

class PostUserReportUseCase
    @Inject
    constructor(
        private val supportRepository: SupportRepository,
    ) {
        suspend operator fun invoke(
            reportedUserId: Long,
            request: PostUserReportRequest,
        ): Result<PostUserReportResponse> = supportRepository.portUserReport(reportedUserId, request)
    }
