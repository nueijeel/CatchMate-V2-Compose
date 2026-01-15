package com.catchmate.data.repository

import com.catchmate.data.datasource.remote.RetrofitClient
import com.catchmate.data.datasource.remote.UserRemoteDataSource
import com.catchmate.data.datasource.remote.UserService
import com.catchmate.data.mapper.UserMapper
import com.catchmate.data.util.ApiResponseHandleUtil.apiCall
import com.catchmate.domain.exception.UserBlockFailureException
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
import com.catchmate.domain.repository.UserRepository
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

class UserRepositoryImpl
    @Inject
    constructor(
        retrofitClient: RetrofitClient,
        private val userRemoteDataSource: UserRemoteDataSource,
    ) : UserRepository {
        private val userApi = retrofitClient.createApi<UserService>()
        private val tag = "UserRepo"

        override suspend fun getUserProfile(): Result<GetUserProfileResponse> =
            apiCall(
                tag = this.tag,
                apiFunction = { userApi.getUserProfile() },
                transform = { UserMapper.toGetUserProfileResponse(it!!) },
            )

        override suspend fun getUserProfileById(profileUserId: Long): Result<GetUserProfileByIdResponse> =
            apiCall(
                tag = this.tag,
                apiFunction = { userApi.getUserProfileById(profileUserId) },
                transform = { UserMapper.toGetUserProfileByIdResponse(it!!) },
            )

        override suspend fun getBlockedUserList(page: Int): Result<GetBlockedUserListResponse> =
            apiCall(
                tag = this.tag,
                apiFunction = { userApi.getBlockedUserList(page) },
                transform = { UserMapper.toGetBlockedUserListResponse(it!!) },
            )

        override suspend fun getUnreadInfo(): Result<GetUnreadInfoResponse> =
            apiCall(
                tag = this.tag,
                apiFunction = { userApi.getUnreadInfo() },
                transform = { UserMapper.toGetUnreadInfoResponse(it!!) },
            )

        override suspend fun postUserBlock(blockedUserId: Long): Result<PostUserBlockResponse> =
            apiCall(
                tag = this.tag,
                apiFunction = { userApi.postUserBlock(blockedUserId) },
                transform = { UserMapper.toPostUserBlockResponse(it!!) },
                errorHandler = { response, jsonObject ->
                    if (response.code() == 400) {
                        val message = jsonObject.getString("message")
                        UserBlockFailureException(message)
                    } else {
                        Exception("$jsonObject")
                    }
                },
            )

        override suspend fun postUserAdditionalInfo(
            postUserAdditionalInfoRequest: PostUserAdditionalInfoRequest,
        ): Result<PostUserAdditionalInfoResponse> =
            apiCall(
                tag = this.tag,
                apiFunction =
                    {
                        userApi.postUserAdditionalInfo(
                            UserMapper.toPostUserAdditionalInfoRequestDTO(
                                postUserAdditionalInfoRequest,
                            ),
                        )
                    },
                transform = { UserMapper.toPostUserAdditionalInfoResponse(it!!) },
            )

        override suspend fun patchUserProfile(
            request: RequestBody,
            profileImage: MultipartBody.Part,
        ): Result<PatchUserProfileResponse> =
            apiCall(
                tag = this.tag,
                apiFunction = { userApi.patchUserProfile(request, profileImage) },
                transform = { UserMapper.toPatchUserProfileResponse(it!!) },
            )

        override suspend fun patchUserAlarm(
            alarmType: String,
            isEnabled: String,
        ): Result<PatchUserAlarmResponse> =
            apiCall(
                tag = this.tag,
                apiFunction = { userApi.patchUserAlarm(alarmType, isEnabled) },
                transform = { UserMapper.toPatchUserAlarmResponse(it!!) },
            )

        override suspend fun deleteBlockedUser(blockedUserId: Long): Result<DeleteBlockedUserResponse> =
            apiCall(
                tag = this.tag,
                apiFunction = { userApi.deleteBlockedUser(blockedUserId) },
                transform = { UserMapper.toDeleteBlockedUserResponse(it!!) },
            )

        override suspend fun deleteUserAccount(): Result<DeleteUserAccountResponse> =
            apiCall(
                tag = this.tag,
                apiFunction = { userApi.deleteUserAccount() },
                transform = { UserMapper.toDeleteUserAccountResponse(it!!) },
            )

        // v2
        override suspend fun saveUser(uid: String, user: UserEntity): Result<Unit> {
            return try {
                val userDto = UserMapper.toUserDto(user)

                userRemoteDataSource.saveUser(uid, userDto)

                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
