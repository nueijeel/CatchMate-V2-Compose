package com.catchmate.domain.repository

import com.catchmate.domain.model.auth.UserEntity
import com.catchmate.domain.model.user.DeleteBlockedUserResponse
import com.catchmate.domain.model.user.DeleteUserAccountResponse
import com.catchmate.domain.model.user.GetBlockedUserListResponse
import com.catchmate.domain.model.user.GetUnreadInfoResponse
import com.catchmate.domain.model.user.GetUserProfileByIdResponse
import com.catchmate.domain.model.user.GetUserProfileResponse
import com.catchmate.domain.model.user.PatchUserAlarmResponse
import com.catchmate.domain.model.user.PatchUserProfileResponse
import com.catchmate.domain.model.user.PostUserAdditionalInfoRequest
import com.catchmate.domain.model.user.PostUserAdditionalInfoResponse
import com.catchmate.domain.model.user.PostUserBlockResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody

interface UserRepository {
    suspend fun getUserProfile(): Result<GetUserProfileResponse>

    suspend fun getUserProfileById(profileUserId: Long): Result<GetUserProfileByIdResponse>

    suspend fun getBlockedUserList(page: Int): Result<GetBlockedUserListResponse>

    suspend fun getUnreadInfo(): Result<GetUnreadInfoResponse>

    suspend fun postUserBlock(blockedUserId: Long): Result<PostUserBlockResponse>

    suspend fun postUserAdditionalInfo(postUserAdditionalInfoRequest: PostUserAdditionalInfoRequest): Result<PostUserAdditionalInfoResponse>

    suspend fun patchUserProfile(
        request: RequestBody,
        profileImage: MultipartBody.Part,
    ): Result<PatchUserProfileResponse>

    suspend fun patchUserAlarm(
        alarmType: String,
        isEnabled: String,
    ): Result<PatchUserAlarmResponse>

    suspend fun deleteBlockedUser(blockedUserId: Long): Result<DeleteBlockedUserResponse>

    suspend fun deleteUserAccount(): Result<DeleteUserAccountResponse>

    // v2
    suspend fun saveUser(uid: String, user: UserEntity): Result<Unit>
}
