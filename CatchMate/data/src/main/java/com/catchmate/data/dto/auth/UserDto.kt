package com.catchmate.data.dto.auth

data class UserDto(
    val email: String,
    val profileImage: String?,
    val nickname: String,
    val favoriteClubId: Int,
    val gender: String,
    val watchStyle: String?,
    val fcmToken: String,
    val birthDate: String,
)
