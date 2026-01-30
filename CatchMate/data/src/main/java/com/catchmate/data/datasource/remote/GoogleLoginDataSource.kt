package com.catchmate.data.datasource.remote

import android.app.Activity
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import com.catchmate.data.BuildConfig
import com.catchmate.domain.exception.GoogleLoginException
import com.catchmate.domain.exception.Result
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import javax.inject.Inject

class GoogleLoginDataSource @Inject constructor() {
    suspend fun getGoogleIdToken(activity: Activity): Result<String> {
        val credentialManager = CredentialManager.create(activity)

        val googleIdOption: GetGoogleIdOption =
            GetGoogleIdOption
                .Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(BuildConfig.GOOGLE_WEB_CLIENT_ID)
                .setAutoSelectEnabled(true)
                .build()

        val request: GetCredentialRequest =
            GetCredentialRequest
                .Builder()
                .addCredentialOption(googleIdOption)
                .build()

        return try {
            val result = credentialManager.getCredential(activity, request)
            val credential = result.credential

            if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                val idToken = GoogleIdTokenCredential.createFrom(credential.data).idToken
                Result.Success(idToken)
            } else {
                Result.Error(exception = GoogleLoginException.TokenParsing)
            }
        } catch (e: GetCredentialException) {
            Log.e("GoogleLoginError", "에러 타입: ${e.type}")
            Log.e("GoogleLoginError", "에러 메시지: ${e.message}")
            Result.Error(exception = e)
        }
    }
}
