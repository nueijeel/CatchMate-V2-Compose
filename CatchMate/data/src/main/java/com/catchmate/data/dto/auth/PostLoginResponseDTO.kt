package com.catchmate.data.dto.auth

data class PostLoginResponseDTO(
    val accessToken: String?,
    val refreshToken: String?,
    val isFirstLogin: Boolean,
)
