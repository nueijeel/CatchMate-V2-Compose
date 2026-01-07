package com.catchmate.domain.usecase.support

import com.catchmate.domain.model.support.GetNoticeListResponse
import com.catchmate.domain.repository.SupportRepository
import javax.inject.Inject

class GetNoticeListUseCase
    @Inject
    constructor(
        private val supportRepository: SupportRepository,
    ) {
        suspend operator fun invoke(page: Int): Result<GetNoticeListResponse> = supportRepository.getNoticeList(page)
    }
