package com.catchmate.data.datasource.remote.impl

import com.catchmate.data.datasource.remote.UserRemoteDataSource
import com.catchmate.data.dto.auth.UserDto
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserRemoteDataSourceImpl
@Inject
constructor(
    private val firestore: FirebaseFirestore
) : UserRemoteDataSource {
    // 유저 데이터 저장
    override suspend fun saveUser(uid: String, userDto: UserDto) {
        firestore.collection("users")
            .document(uid)
            .set(userDto)
            .await()
    }
}
