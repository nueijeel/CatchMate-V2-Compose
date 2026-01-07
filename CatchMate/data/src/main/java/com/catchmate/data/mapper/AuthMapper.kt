package com.catchmate.data.mapper

import com.catchmate.data.dto.auth.DeleteLogoutResponseDTO
import com.catchmate.data.dto.auth.GetCheckNicknameResponseDTO
import com.catchmate.data.dto.auth.PostLoginRequestDTO
import com.catchmate.data.dto.auth.PostLoginResponseDTO
import com.catchmate.domain.model.auth.DeleteLogoutResponse
import com.catchmate.domain.model.auth.GetCheckNicknameResponse
import com.catchmate.domain.model.auth.PostLoginRequest
import com.catchmate.domain.model.auth.PostLoginResponse

object AuthMapper {
    fun toPostLoginRequestDTO(postLoginRequest: PostLoginRequest): PostLoginRequestDTO =
        PostLoginRequestDTO(
            providerId = postLoginRequest.providerId,
            provider = postLoginRequest.provider,
            email = postLoginRequest.email,
            picture = postLoginRequest.picture,
            fcmToken = postLoginRequest.fcmToken,
        )

    fun toPostLoginRequest(postLoginRequestDTO: PostLoginRequestDTO?): PostLoginRequest? =
        if (postLoginRequestDTO == null) {
            null
        } else {
            PostLoginRequest(
                providerId = postLoginRequestDTO.providerId,
                provider = postLoginRequestDTO.provider,
                email = postLoginRequestDTO.email,
                picture = postLoginRequestDTO.picture,
                fcmToken = postLoginRequestDTO.fcmToken,
            )
        }

    fun toGooglePostLoginRequest(postLoginRequestDTO: PostLoginRequestDTO): PostLoginRequest =
        PostLoginRequest(
            providerId = postLoginRequestDTO.providerId,
            provider = postLoginRequestDTO.provider,
            email = postLoginRequestDTO.email,
            picture = postLoginRequestDTO.picture,
            fcmToken = postLoginRequestDTO.fcmToken,
        )

    fun toPostLoginResponse(postLoginResponseDTO: PostLoginResponseDTO): PostLoginResponse =
        PostLoginResponse(
            accessToken = postLoginResponseDTO.accessToken,
            refreshToken = postLoginResponseDTO.refreshToken,
            isFirstLogin = postLoginResponseDTO.isFirstLogin,
        )

    fun toGetCheckNicknameResponse(getCheckNicknameResponseDTO: GetCheckNicknameResponseDTO): GetCheckNicknameResponse =
        GetCheckNicknameResponse(
            available = getCheckNicknameResponseDTO.available,
        )

    fun toDeleteLogoutResponse(responseDTO: DeleteLogoutResponseDTO): DeleteLogoutResponse =
        DeleteLogoutResponse(
            state = responseDTO.state,
        )
}
