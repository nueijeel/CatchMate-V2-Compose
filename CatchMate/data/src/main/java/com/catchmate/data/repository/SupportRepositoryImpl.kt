package com.catchmate.data.repository

import com.catchmate.data.datasource.remote.RetrofitClient
import com.catchmate.data.datasource.remote.SupportService
import com.catchmate.data.mapper.SupportMapper
import com.catchmate.data.util.ApiResponseHandleUtil.apiCall
import com.catchmate.domain.model.support.GetInquiryResponse
import com.catchmate.domain.model.support.GetNoticeListResponse
import com.catchmate.domain.model.support.NoticeInfo
import com.catchmate.domain.model.support.PostInquiryRequest
import com.catchmate.domain.model.support.PostInquiryResponse
import com.catchmate.domain.model.support.PostUserReportRequest
import com.catchmate.domain.model.support.PostUserReportResponse
import com.catchmate.domain.repository.SupportRepository
import javax.inject.Inject

class SupportRepositoryImpl
    @Inject
    constructor(
        retrofitClient: RetrofitClient,
    ) : SupportRepository {
        private val supportApi = retrofitClient.createApi<SupportService>()
        private val tag = "SupportRepo"

        override suspend fun getInquiry(inquiryId: Long): Result<GetInquiryResponse> =
            apiCall(
                tag = this.tag,
                apiFunction = { supportApi.getInquiry(inquiryId) },
                transform = { SupportMapper.toGetInquiryResponse(it!!) },
            )

        override suspend fun postInquiry(request: PostInquiryRequest): Result<PostInquiryResponse> =
            apiCall(
                tag = this.tag,
                apiFunction = { supportApi.postInquiry(SupportMapper.toPostInquiryRequestDTO(request)) },
                transform = { SupportMapper.toPostInquiryResponse(it!!) },
            )

        override suspend fun portUserReport(
            reportedUserId: Long,
            request: PostUserReportRequest,
        ): Result<PostUserReportResponse> =
            apiCall(
                tag = this.tag,
                apiFunction = { supportApi.postUserReport(reportedUserId, SupportMapper.toPostUserReportRequestDTO(request)) },
                transform = { SupportMapper.toPostUserReportResponse(it!!) },
            )

        override suspend fun getNoticeList(page: Int): Result<GetNoticeListResponse> =
            apiCall(
                tag = this.tag,
                apiFunction = { supportApi.getNoticeList(page) },
                transform = { SupportMapper.toGetNoticeListResponse(it!!) },
            )

        override suspend fun getNoticeDetail(noticeId: Long): Result<NoticeInfo> =
            apiCall(
                tag = this.tag,
                apiFunction = { supportApi.getNoticeDetail(noticeId) },
                transform = { SupportMapper.toNoticeInfo(it!!) },
            )
    }
