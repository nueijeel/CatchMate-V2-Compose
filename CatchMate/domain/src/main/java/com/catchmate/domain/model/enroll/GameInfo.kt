package com.catchmate.domain.model.enroll

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class GameInfo(
    val homeClubId: Int,
    val awayClubId: Int,
    val gameStartDate: String?,
    val location: String,
) : Parcelable
