package com.catchmate.data.datasource.remote

import com.catchmate.data.dto.chatting.ChatRoomInfoDTO
import com.catchmate.data.dto.chatting.DeleteChattingCrewKickOutResponseDTO
import com.catchmate.data.dto.chatting.DeleteChattingRoomResponseDTO
import com.catchmate.data.dto.chatting.GetChattingCrewListResponseDTO
import com.catchmate.data.dto.chatting.GetChattingHistoryResponseDTO
import com.catchmate.data.dto.chatting.GetChattingRoomListResponseDTO
import com.catchmate.data.dto.chatting.PatchChattingRoomImageResponseDTO
import com.catchmate.data.dto.chatting.PutChattingRoomAlarmResponseDTO
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ChattingService {
    @GET("chat-rooms/list")
    suspend fun getChattingRoomList(
        @Query("page") page: Int,
    ): Response<GetChattingRoomListResponseDTO?>

    @GET("chat-rooms/{chatRoomId}/user-list")
    suspend fun getChattingCrewList(
        @Path("chatRoomId") chatRoomId: Long,
    ): Response<GetChattingCrewListResponseDTO?>

    @GET("chat-rooms/{chatRoomId}")
    suspend fun getChattingRoomInfo(
        @Path("chatRoomId") chatRoomId: Long,
    ): Response<ChatRoomInfoDTO?>

    @Multipart
    @PATCH("chat-rooms/{chatRoomId}/image")
    suspend fun patchChattingRoomImage(
        @Path("chatRoomId") chatRoomId: Long,
        @Part chatRoomImage: MultipartBody.Part,
    ): Response<PatchChattingRoomImageResponseDTO?>

    @PUT("chat-rooms/{chatRoomId}/notification")
    suspend fun putChattingRoomAlarm(
        @Path("chatRoomId") chatRoomId: Long,
        @Query("enable") enable: Boolean,
    ): Response<PutChattingRoomAlarmResponseDTO?>

    @DELETE("chat-rooms/{chatRoomId}")
    suspend fun deleteChattingRoom(
        @Path("chatRoomId") chatRoomId: Long,
    ): Response<DeleteChattingRoomResponseDTO?>

    @DELETE("chat-rooms/{chatRoomId}/users/{userId}")
    suspend fun deleteChattingCrewKickOut(
        @Path("chatRoomId") chatRoomId: Long,
        @Path("userId") userId: Long,
    ): Response<DeleteChattingCrewKickOutResponseDTO?>

    @GET("chats/{chatRoomId}")
    suspend fun getChattingHistory(
        @Path("chatRoomId") chatRoomId: Long,
        @Query("lastMessageId") lastMessageId: String?,
        @Query("size") size: Int?, // default = 20
    ): Response<GetChattingHistoryResponseDTO?>
}
