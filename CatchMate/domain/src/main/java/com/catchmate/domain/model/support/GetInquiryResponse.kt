package com.catchmate.domain.model.support

data class GetInquiryResponse(
    val inquiryId: Long,
    val inquiryType: String,
    val content: String,
    val nickName: String,
    val answer: String,
    val isCompleted: Boolean,
    val createdAt: String,
)
