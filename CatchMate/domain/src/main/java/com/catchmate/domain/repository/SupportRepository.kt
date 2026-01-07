package com.catchmate.domain.repository

import com.catchmate.domain.model.support.GetInquiryResponse
import com.catchmate.domain.model.support.GetNoticeListResponse
import com.catchmate.domain.model.support.NoticeInfo
import com.catchmate.domain.model.support.PostInquiryRequest
import com.catchmate.domain.model.support.PostInquiryResponse
import com.catchmate.domain.model.support.PostUserReportRequest
import com.catchmate.domain.model.support.PostUserReportResponse

interface SupportRepository {
    suspend fun getInquiry(inquiryId: Long): Result<GetInquiryResponse>

    suspend fun postInquiry(request: PostInquiryRequest): Result<PostInquiryResponse>

    suspend fun portUserReport(
        reportedUserId: Long,
        request: PostUserReportRequest,
    ): Result<PostUserReportResponse>

    suspend fun getNoticeList(page: Int): Result<GetNoticeListResponse>

    suspend fun getNoticeDetail(noticeId: Long): Result<NoticeInfo>
}
