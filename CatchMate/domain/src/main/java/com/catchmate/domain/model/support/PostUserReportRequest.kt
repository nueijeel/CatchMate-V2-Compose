package com.catchmate.domain.model.support

data class PostUserReportRequest(
    val reportType: String,
    val content: String,
)
