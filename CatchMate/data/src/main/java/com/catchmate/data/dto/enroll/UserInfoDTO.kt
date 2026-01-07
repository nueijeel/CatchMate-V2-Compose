package com.catchmate.data.dto.enroll

import com.catchmate.data.dto.user.FavoriteClubDTO

data class UserInfoDTO(
    val userId: Long,
    val email: String,
    val profileImageUrl: String,
    val gender: String,
    val allAlarm: String,
    val chatAlarm: String,
    val enrollAlarm: String,
    val eventAlarm: String,
    val nickName: String,
    val favoriteClub: FavoriteClubDTO,
    val birthDate: String,
    val watchStyle: String? = null,
)
