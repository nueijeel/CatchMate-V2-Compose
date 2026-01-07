package com.catchmate.domain.model.support

import com.catchmate.domain.model.user.FavoriteClub

data class AdminUserInfo(
    val userId: Long,
    val profileImageUrl: String,
    val nickName: String,
    val clubInfo: FavoriteClub,
    val gender: String,
    val email: String,
    val socialType: String,
    val joinedAt: String,
)
