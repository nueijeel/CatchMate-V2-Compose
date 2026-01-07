package com.catchmate.domain.model.chatting

import android.os.Parcelable
import com.catchmate.domain.model.user.GetUserProfileResponse
import kotlinx.parcelize.Parcelize

@Parcelize
data class GetChattingCrewListResponse(
    val userInfoList: List<GetUserProfileResponse>,
) : Parcelable
