package com.catchmate.domain.usecase.support

import com.catchmate.domain.model.support.PostInquiryRequest
import com.catchmate.domain.model.support.PostInquiryResponse
import com.catchmate.domain.repository.SupportRepository
import javax.inject.Inject

class PostInquiryUseCase
    @Inject
    constructor(
        private val supportRepository: SupportRepository,
    ) {
        suspend operator fun invoke(request: PostInquiryRequest): Result<PostInquiryResponse> = supportRepository.postInquiry(request)
    }
