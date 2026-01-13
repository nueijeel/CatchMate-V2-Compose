package com.catchmate.data.repository

import android.app.Activity
import android.util.Log
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.NoCredentialException
import com.catchmate.data.datasource.local.GoogleLoginDataSource
import com.catchmate.domain.exception.GoogleLoginException
import com.catchmate.domain.exception.Result
import com.catchmate.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImpl
@Inject
constructor(
    private val firebaseAuth: FirebaseAuth,
    private val googleLoginDataSource: GoogleLoginDataSource,
) : AuthRepository {
    override suspend fun signInWithGoogle(activity: Activity): Result<String> {
        return try {
            val idToken = googleLoginDataSource.getGoogleIdToken(activity)

            if (idToken is Result.Success) {
                val realToken = idToken.data
                Log.d("TokenCheck", "전달되는 토큰: $realToken")
                val credential = GoogleAuthProvider.getCredential(realToken, null)
                val authResult = firebaseAuth.signInWithCredential(credential).await()

                if (authResult.user != null) {
                    Result.Success("${authResult.user?.uid} / ${authResult.user?.email}")
                } else {
                    Result.Error(exception = GoogleLoginException.Unknown(Exception("Firebase user is null")))
                }
            } else {
                Result.Error(exception = GoogleLoginException.Unknown(Exception("Token is not String")))
            }
        } catch (e: Exception) {
            val exception = when (e) {
                is GetCredentialCancellationException, is GoogleLoginException.Cancelled -> GoogleLoginException.Cancelled
                is NoCredentialException, is GoogleLoginException.NoCredentials -> GoogleLoginException.NoCredentials
                is GoogleLoginException.TokenParsing -> GoogleLoginException.TokenParsing
                else -> {
                    Log.e("GoogleLoginError", "원인: ${e.message}", e)
                    GoogleLoginException.Unknown(e)
                }
            }
            Result.Error(exception = exception)
        }
    }
}
