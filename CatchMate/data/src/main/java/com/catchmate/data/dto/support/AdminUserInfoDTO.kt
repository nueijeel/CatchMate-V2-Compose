package com.catchmate.data.dto.support

import com.catchmate.data.dto.user.FavoriteClubDTO

data class AdminUserInfoDTO(
    val userId: Long,
    val profileImageUrl: String,
    val nickName: String,
    val clubInfo: FavoriteClubDTO,
    val gender: String,
    val email: String,
    val socialType: String,
    val joinedAt: String,
)
