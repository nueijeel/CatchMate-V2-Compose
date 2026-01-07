package com.catchmate.data.datasource.remote

import com.catchmate.data.dto.enroll.DeleteEnrollResponseDTO
import com.catchmate.data.dto.enroll.GetAllReceivedEnrollResponseDTO
import com.catchmate.data.dto.enroll.GetEnrollNewCountResponseDTO
import com.catchmate.data.dto.enroll.GetReceivedEnrollResponseDTO
import com.catchmate.data.dto.enroll.GetRequestedEnrollListResponseDTO
import com.catchmate.data.dto.enroll.GetRequestedEnrollResponseDTO
import com.catchmate.data.dto.enroll.PatchEnrollAcceptResponseDTO
import com.catchmate.data.dto.enroll.PatchEnrollRejectResponseDTO
import com.catchmate.data.dto.enroll.PostEnrollRequestDTO
import com.catchmate.data.dto.enroll.PostEnrollResponseDTO
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface EnrollService {
    @POST("enrolls/{boardId}")
    suspend fun postEnroll(
        @Path("boardId") boardId: Long,
        @Body postEnrollRequestDTO: PostEnrollRequestDTO,
    ): Response<PostEnrollResponseDTO?>

    @PATCH("enrolls/{enrollId}/reject")
    suspend fun patchEnrollReject(
        @Path("enrollId") enrollId: Long,
    ): Response<PatchEnrollRejectResponseDTO?>

    @PATCH("enrolls/{enrollId}/accept")
    suspend fun patchEnrollAccept(
        @Path("enrollId") enrollId: Long,
    ): Response<PatchEnrollAcceptResponseDTO?>

    @GET("enrolls/{boardId}/description")
    suspend fun getRequestedEnroll(
        @Path("boardId") boardId: Long,
    ): Response<GetRequestedEnrollResponseDTO?>

    @GET("enrolls/request")
    suspend fun getRequestedEnrollList(
        @Query("page") page: Int,
    ): Response<GetRequestedEnrollListResponseDTO?>

    @GET("enrolls/receive")
    suspend fun getReceivedEnroll(
        @Query("boardId") boardId: Long,
    ): Response<GetReceivedEnrollResponseDTO?>

    @GET("enrolls/receive/all")
    suspend fun getAllReceivedEnroll(
        @Query("page") page: Int,
    ): Response<GetAllReceivedEnrollResponseDTO?>

    @GET("enrolls/new-count")
    suspend fun getEnrollNewCount(): Response<GetEnrollNewCountResponseDTO?>

    @DELETE("enrolls/cancel/{enrollId}")
    suspend fun deleteEnroll(
        @Path("enrollId") enrollId: Long,
    ): Response<DeleteEnrollResponseDTO?>
}
