package com.catchmate.data.dto.chatting

import com.catchmate.data.dto.user.GetUserProfileResponseDTO

data class GetChattingCrewListResponseDTO(
    val userInfoList: List<GetUserProfileResponseDTO>,
)
