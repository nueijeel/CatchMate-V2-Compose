package com.catchmate.domain.usecase.support

import com.catchmate.domain.model.support.NoticeInfo
import com.catchmate.domain.repository.SupportRepository
import javax.inject.Inject

class GetNoticeDetailUseCase
    @Inject
    constructor(
        private val supportRepository: SupportRepository,
    ) {
        suspend operator fun invoke(noticeId: Long): Result<NoticeInfo> = supportRepository.getNoticeDetail(noticeId)
    }
