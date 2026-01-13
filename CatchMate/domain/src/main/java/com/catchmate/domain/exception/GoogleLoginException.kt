package com.catchmate.domain.exception

sealed class GoogleLoginException(
    message: String,
) : Exception(message) {
    object Cancelled : GoogleLoginException("로그인이 취소되었습니다.")

    object NoCredentials : GoogleLoginException("자격증명을 찾을 수 없습니다.")

    object TokenParsing : GoogleLoginException("토큰 파싱에 실패했습니다.")

    data class Unknown(
        val originalException: Throwable,
    ) : GoogleLoginException("알 수 없는 오류가 발생했습니다.")
}

sealed class Result<out T> {
    data class Success<T>(
        val data: T,
    ) : Result<T>()

    data class Error(
        val code: String? = null,
        val message: String? = null,
        val exception: Throwable? = null,
    ) : Result<Nothing>()
}