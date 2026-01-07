package com.catchmate.data.mapper

import com.catchmate.data.dto.support.AdminUserInfoDTO
import com.catchmate.data.dto.support.GetInquiryResponseDTO
import com.catchmate.data.dto.support.GetNoticeListResponseDTO
import com.catchmate.data.dto.support.NoticeInfoDTO
import com.catchmate.data.dto.support.PostInquiryRequestDTO
import com.catchmate.data.dto.support.PostInquiryResponseDTO
import com.catchmate.data.dto.support.PostUserReportRequestDTO
import com.catchmate.data.dto.support.PostUserReportResponseDTO
import com.catchmate.data.mapper.BoardMapper.toFavoriteClub
import com.catchmate.domain.model.support.AdminUserInfo
import com.catchmate.domain.model.support.GetInquiryResponse
import com.catchmate.domain.model.support.GetNoticeListResponse
import com.catchmate.domain.model.support.NoticeInfo
import com.catchmate.domain.model.support.PostInquiryRequest
import com.catchmate.domain.model.support.PostInquiryResponse
import com.catchmate.domain.model.support.PostUserReportRequest
import com.catchmate.domain.model.support.PostUserReportResponse

object SupportMapper {
    fun toPostInquiryRequestDTO(request: PostInquiryRequest): PostInquiryRequestDTO =
        PostInquiryRequestDTO(
            inquiryType = request.inquiryType,
            content = request.content,
        )

    fun toPostInquiryResponse(dto: PostInquiryResponseDTO): PostInquiryResponse =
        PostInquiryResponse(
            state = dto.state,
        )

    fun toPostUserReportRequestDTO(request: PostUserReportRequest): PostUserReportRequestDTO =
        PostUserReportRequestDTO(
            reportType = request.reportType,
            content = request.content,
        )

    fun toPostUserReportResponse(dto: PostUserReportResponseDTO): PostUserReportResponse =
        PostUserReportResponse(
            state = dto.state,
        )

    fun toGetInquiryResponse(dto: GetInquiryResponseDTO): GetInquiryResponse =
        GetInquiryResponse(
            inquiryId = dto.inquiryId,
            inquiryType = dto.inquiryType,
            content = dto.content,
            nickName = dto.nickName,
            answer = dto.answer,
            isCompleted = dto.isCompleted,
            createdAt = dto.createdAt,
        )

    fun toGetNoticeListResponse(dto: GetNoticeListResponseDTO): GetNoticeListResponse =
        GetNoticeListResponse(
            noticeInfoList = dto.noticeInfoList.map { toNoticeInfo(it) },
            totalPages = dto.totalPages,
            totalElements = dto.totalElements,
            isFirst = dto.isFirst,
            isLast = dto.isLast,
        )

    fun toNoticeInfo(dto: NoticeInfoDTO): NoticeInfo =
        NoticeInfo(
            noticeId = dto.noticeId,
            title = dto.title,
            content = dto.content,
            userInfo = toAdminUserInfo(dto.userInfo),
            createdAt = dto.createdAt,
            updatedAt = dto.updatedAt,
        )

    fun toAdminUserInfo(dto: AdminUserInfoDTO): AdminUserInfo =
        AdminUserInfo(
            userId = dto.userId,
            profileImageUrl = dto.profileImageUrl,
            nickName = dto.nickName,
            clubInfo = toFavoriteClub(dto.clubInfo),
            gender = dto.gender,
            email = dto.email,
            socialType = dto.socialType,
            joinedAt = dto.joinedAt,
        )
}
