package com.catchmate.domain.model.user

data class UserProfileRequest(
    val nickName: String,
    val favoriteClubId: Int,
    val watchStyle: String,
)
