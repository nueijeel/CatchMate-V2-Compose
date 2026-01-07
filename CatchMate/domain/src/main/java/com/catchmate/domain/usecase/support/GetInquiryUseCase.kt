package com.catchmate.domain.usecase.support

import com.catchmate.domain.model.support.GetInquiryResponse
import com.catchmate.domain.repository.SupportRepository
import javax.inject.Inject

class GetInquiryUseCase
    @Inject
    constructor(
        private val supportRepository: SupportRepository,
    ) {
        suspend operator fun invoke(inquiryId: Long): Result<GetInquiryResponse> = supportRepository.getInquiry(inquiryId)
    }
