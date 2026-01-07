package com.catchmate.data.dto.user

data class PostUserAdditionalInfoResponseDTO(
    val userId: Long,
    val accessToken: String,
    val refreshToken: String,
    val createdAt: String,
)
