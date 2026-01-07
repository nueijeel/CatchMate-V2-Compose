package com.catchmate.data.datasource.local

import android.app.Activity
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.NoCredentialException
import com.catchmate.data.BuildConfig
import com.catchmate.data.datasource.remote.FCMTokenService
import com.catchmate.data.dto.auth.PostLoginRequestDTO
import com.catchmate.domain.exception.GoogleLoginException
import com.catchmate.domain.exception.Result
import com.catchmate.domain.model.enumclass.LoginPlatform
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.util.Collections
import javax.inject.Inject

class GoogleLoginDataSource
    @Inject
    constructor(
        private val fcmTokenService: FCMTokenService,
    ) {
        suspend fun getCredential(activity: Activity): Result<GetCredentialResponse> {
            val credentialManager = CredentialManager.create(activity)

            val googleIdOption: GetGoogleIdOption =
                GetGoogleIdOption
                    .Builder()
                    .setAutoSelectEnabled(true)
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(BuildConfig.GOOGLE_WEB_CLIENT_ID)
                    .build()

            val request =
                GetCredentialRequest
                    .Builder()
                    .addCredentialOption(googleIdOption)
                    .build()

            return try {
                val result = credentialManager.getCredential(activity, request)
                Result.Success(result)
            } catch (e: Exception) {
                when (e) {
                    is GoogleLoginException.NoCredentials,
                    is NoCredentialException,
                    -> {
                        Log.d("GOOGLE - NOCredentials", "")
                        Result.Error(exception = e)
                    }

                    is GoogleLoginException.Cancelled,
                    is GetCredentialCancellationException,
                    -> {
                        Log.d("GOOGLE - Cancelled", "")
                        Result.Error(exception = e)
                    }

                    else -> {
                        Log.d("GOOGLE - ELSE", e.printStackTrace().toString())
                        Result.Error(exception = e)
                    }
                }
            }
        }

        suspend fun handleSignIn(result: GetCredentialResponse): Result<PostLoginRequestDTO> {
            val credential = result.credential

            return if (credential is CustomCredential) {
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    try {
                        val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                        val idToken = googleIdTokenCredential.idToken
                        val email = googleIdTokenCredential.id
                        val profileUri = googleIdTokenCredential.profilePictureUri

                        val verifier =
                            GoogleIdTokenVerifier
                                .Builder(NetHttpTransport(), GsonFactory())
                                .setAudience(Collections.singletonList(BuildConfig.GOOGLE_WEB_CLIENT_ID))
                                .build()
                        val verifiedIdToken =
                            withContext(Dispatchers.IO) {
                                verifier.verify(idToken)
                            }

                        if (verifiedIdToken != null) {
                            val payload = verifiedIdToken.payload
                            val userId = payload.subject

                            Log.i("GoogleInfoSuccess", "idToken : $userId  email : $email profileUri : $profileUri")
                            val loginRequestDTO =
                                PostLoginRequestDTO(
                                    email = email,
                                    providerId = userId,
                                    provider = LoginPlatform.GOOGLE.toString().lowercase(),
                                    picture = profileUri.toString(),
                                    fcmToken =
                                        runBlocking(Dispatchers.IO) {
                                            fcmTokenService.getToken()
                                        },
                                )
                            Result.Success(loginRequestDTO)
                        } else {
                            Log.d("GoogleIdTokenError", "Invalid ID token")
                            Result.Error(message = "Invalid ID token")
                        }
                    } catch (e: GoogleIdTokenParsingException) {
                        Log.d("GoogleInfoError", "Received an invalid google id token response", e)
                        Result.Error(exception = e)
                    } catch (e: Exception) {
                        Log.d("GoogleInfoError", "Unexpected error parsing credentials", e)
                        Result.Error(exception = e)
                    }
                } else {
                    Log.d("GoogleInfoError", "Unexpected type of credential")
                    Result.Error(exception = IllegalArgumentException("Unexpected credential type"))
                }
            } else {
                Log.d("GoogleInfoError", "Unexpected type of credential")
                Result.Error(exception = IllegalArgumentException("Unexpected credential type"))
            }
        }
    }
