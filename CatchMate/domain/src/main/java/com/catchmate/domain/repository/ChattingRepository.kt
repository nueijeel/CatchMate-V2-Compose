package com.catchmate.domain.repository

import com.catchmate.domain.model.chatting.ChatRoomInfo
import com.catchmate.domain.model.chatting.DeleteChattingCrewKickOutResponse
import com.catchmate.domain.model.chatting.DeleteChattingRoomResponse
import com.catchmate.domain.model.chatting.GetChattingCrewListResponse
import com.catchmate.domain.model.chatting.GetChattingHistoryResponse
import com.catchmate.domain.model.chatting.GetChattingRoomListResponse
import com.catchmate.domain.model.chatting.PatchChattingRoomImageResponse
import com.catchmate.domain.model.chatting.PutChattingRoomAlarmResponse
import okhttp3.MultipartBody

interface ChattingRepository {
    suspend fun getChattingRoomList(page: Int): Result<GetChattingRoomListResponse>

    suspend fun getChattingCrewList(chatRoomId: Long): Result<GetChattingCrewListResponse>

    suspend fun getChattingRoomInfo(chatRoomId: Long): Result<ChatRoomInfo>

    suspend fun patchChattingRoomImage(
        chatRoomId: Long,
        chatRoomImage: MultipartBody.Part,
    ): Result<PatchChattingRoomImageResponse>

    suspend fun putChattingRoomAlarm(
        chatRoomId: Long,
        enable: Boolean,
    ): Result<PutChattingRoomAlarmResponse>

    suspend fun deleteChattingRoom(chatRoomId: Long): Result<DeleteChattingRoomResponse>

    suspend fun deleteChattingCrewKickOut(
        chatRoomId: Long,
        userId: Long,
    ): Result<DeleteChattingCrewKickOutResponse>

    suspend fun getChattingHistory(
        chatRoomId: Long,
        lastMessageId: String?,
        size: Int?,
    ): Result<GetChattingHistoryResponse>
}
