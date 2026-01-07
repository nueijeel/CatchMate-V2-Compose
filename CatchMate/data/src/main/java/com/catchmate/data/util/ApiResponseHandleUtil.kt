package com.catchmate.data.util

import com.catchmate.domain.exception.ReissueFailureException
import org.json.JSONObject
import retrofit2.Response

object ApiResponseHandleUtil {
    private fun <T, R> Response<T>.handleApiResponse(
        transform: (T) -> R,
        errorHandler: (Response<T>, JSONObject) -> Exception,
    ): Result<R> =
        try {
            if (isSuccessful) {
                val body = body()?.let { transform(it) } ?: throw NullPointerException("Null Response")
                Result.success(body)
            } else {
                val errorJson = JSONObject(errorBody()?.string() ?: "")
                Result.failure(errorHandler(this, errorJson))
            }
        } catch (e: ReissueFailureException) {
            Result.failure(e)
        } catch (e: Exception) {
            Result.failure(e)
        }

    suspend fun <T, R> apiCall(
        tag: String,
        apiFunction: suspend () -> Response<T>,
        transform: (T) -> R,
        errorHandler: (Response<T>, JSONObject) -> Exception =
            { response, json ->
                Exception("$tag 통신 실패: ${response.code()} - $json")
            },
    ): Result<R> =
        try {
            apiFunction().handleApiResponse(transform, errorHandler)
        } catch (e: Exception) {
            Result.failure(e)
        }
}
