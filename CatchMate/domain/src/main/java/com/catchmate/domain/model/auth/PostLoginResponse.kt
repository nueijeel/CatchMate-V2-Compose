package com.catchmate.domain.model.auth

data class PostLoginResponse(
    val accessToken: String?,
    val refreshToken: String?,
    val isFirstLogin: Boolean,
)
