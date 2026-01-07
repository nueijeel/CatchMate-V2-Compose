package com.catchmate.domain.model.user

import java.io.Serializable

data class PostUserAdditionalInfoRequest(
    val email: String,
    val providerId: String,
    val provider: String,
    val profileImageUrl: String,
    val fcmToken: String,
    val gender: String,
    val nickName: String,
    val birthDate: String,
    val favoriteClubId: Int,
    val watchStyle: String,
) : Serializable
