package com.catchmate.domain.model.board

import android.os.Parcelable
import com.catchmate.domain.model.enroll.GameInfo
import com.catchmate.domain.model.enroll.UserInfo
import kotlinx.parcelize.Parcelize

@Parcelize
data class PatchBoardResponse(
    val boardId: Long,
    val title: String,
    val content: String,
    val cheerClubId: Int,
    val currentPerson: Int,
    val maxPerson: Int,
    val preferredGender: String,
    val preferredAgeRange: String,
    val liftUpDate: String,
    val gameInfo: GameInfo,
    val userInfo: UserInfo,
    val buttonStatus: String?,
    val bookMarked: Boolean,
) : Parcelable
