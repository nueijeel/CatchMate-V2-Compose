package com.catchmate.data.datasource.remote

import com.catchmate.data.dto.user.DeleteBlockedUserResponseDTO
import com.catchmate.data.dto.user.DeleteUserAccountResponseDTO
import com.catchmate.data.dto.user.GetBlockedUserListResponseDTO
import com.catchmate.data.dto.user.GetUnreadInfoResponseDTO
import com.catchmate.data.dto.user.GetUserProfileByIdResponseDTO
import com.catchmate.data.dto.user.GetUserProfileResponseDTO
import com.catchmate.data.dto.user.PatchUserAlarmResponseDTO
import com.catchmate.data.dto.user.PatchUserProfileResponseDTO
import com.catchmate.data.dto.user.PostUserAdditionalInfoRequestDTO
import com.catchmate.data.dto.user.PostUserAdditionalInfoResponseDTO
import com.catchmate.data.dto.user.PostUserBlockResponseDTO
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface UserService {
    @GET("users/profile")
    suspend fun getUserProfile(): Response<GetUserProfileResponseDTO?>

    @GET("users/profile/{profileUserId}")
    suspend fun getUserProfileById(
        @Path("profileUserId") profileUserId: Long,
    ): Response<GetUserProfileByIdResponseDTO?>

    @GET("users/block")
    suspend fun getBlockedUserList(
        @Query("page") page: Int,
    ): Response<GetBlockedUserListResponseDTO?>

    @GET("users/has-unread")
    suspend fun getUnreadInfo(): Response<GetUnreadInfoResponseDTO?>

    @POST("users/block/{blockedUserId}")
    suspend fun postUserBlock(
        @Path("blockedUserId") blockedUserId: Long,
    ): Response<PostUserBlockResponseDTO?>

    @POST("users/additional-info")
    suspend fun postUserAdditionalInfo(
        @Body postUserAdditionalInfoRequestDTO: PostUserAdditionalInfoRequestDTO,
    ): Response<PostUserAdditionalInfoResponseDTO?>

    @Multipart
    @PATCH("users/profile")
    suspend fun patchUserProfile(
        @Part("request") request: RequestBody,
        @Part profileImage: MultipartBody.Part,
    ): Response<PatchUserProfileResponseDTO?>

    // isEnabled : Y/N
    @PATCH("users/alarm")
    suspend fun patchUserAlarm(
        @Query("alarmType") alarmType: String,
        @Query("isEnabled") isEnabled: String,
    ): Response<PatchUserAlarmResponseDTO?>

    @DELETE("users/block/{blockedUserId}")
    suspend fun deleteBlockedUser(
        @Path("blockedUserId") blockedUserId: Long,
    ): Response<DeleteBlockedUserResponseDTO?>

    @DELETE("users/withdraw")
    suspend fun deleteUserAccount(): Response<DeleteUserAccountResponseDTO?>
}
