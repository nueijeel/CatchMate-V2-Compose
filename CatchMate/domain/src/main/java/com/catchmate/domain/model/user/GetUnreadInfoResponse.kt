package com.catchmate.domain.model.user

data class GetUnreadInfoResponse(
    val hasUnreadChat: Boolean,
    val hasUnreadNotification: Boolean,
)
