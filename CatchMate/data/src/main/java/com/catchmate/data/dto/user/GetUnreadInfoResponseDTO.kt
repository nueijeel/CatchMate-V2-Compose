package com.catchmate.data.dto.user

data class GetUnreadInfoResponseDTO(
    val hasUnreadChat: Boolean,
    val hasUnreadNotification: Boolean,
)
