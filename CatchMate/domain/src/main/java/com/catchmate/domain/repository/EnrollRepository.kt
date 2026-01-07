package com.catchmate.domain.repository

import com.catchmate.domain.model.enroll.DeleteEnrollResponse
import com.catchmate.domain.model.enroll.GetAllReceivedEnrollResponse
import com.catchmate.domain.model.enroll.GetEnrollNewCountResponse
import com.catchmate.domain.model.enroll.GetReceivedEnrollResponse
import com.catchmate.domain.model.enroll.GetRequestedEnrollListResponse
import com.catchmate.domain.model.enroll.GetRequestedEnrollResponse
import com.catchmate.domain.model.enroll.PatchEnrollAcceptResponse
import com.catchmate.domain.model.enroll.PatchEnrollRejectResponse
import com.catchmate.domain.model.enroll.PostEnrollRequest
import com.catchmate.domain.model.enroll.PostEnrollResponse

interface EnrollRepository {
    suspend fun postEnroll(
        boardId: Long,
        postEnrollRequest: PostEnrollRequest,
    ): Result<PostEnrollResponse>

    suspend fun patchEnrollReject(enrollId: Long): Result<PatchEnrollRejectResponse>

    suspend fun patchEnrollAccept(enrollId: Long): Result<PatchEnrollAcceptResponse>

    suspend fun getRequestedEnroll(boardId: Long): Result<GetRequestedEnrollResponse>

    suspend fun getRequestedEnrollList(page: Int): Result<GetRequestedEnrollListResponse>

    suspend fun getReceivedEnroll(boardId: Long): Result<GetReceivedEnrollResponse>

    suspend fun getAllReceivedEnroll(page: Int): Result<GetAllReceivedEnrollResponse>

    suspend fun getEnrollNewCount(): Result<GetEnrollNewCountResponse>

    suspend fun deleteEnroll(enrollId: Long): Result<DeleteEnrollResponse>
}
