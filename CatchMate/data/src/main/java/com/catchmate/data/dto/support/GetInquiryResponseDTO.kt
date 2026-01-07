package com.catchmate.data.dto.support

data class GetInquiryResponseDTO(
    val inquiryId: Long,
    val inquiryType: String,
    val content: String,
    val nickName: String,
    val answer: String,
    val isCompleted: Boolean,
    val createdAt: String,
)
