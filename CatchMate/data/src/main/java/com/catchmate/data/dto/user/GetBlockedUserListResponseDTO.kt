package com.catchmate.data.dto.user

data class GetBlockedUserListResponseDTO(
    val userInfoList: List<GetUserProfileResponseDTO>,
    val totalPages: Int,
    val totalElements: Int,
    val isFirst: Boolean,
    val isLast: Boolean,
)
