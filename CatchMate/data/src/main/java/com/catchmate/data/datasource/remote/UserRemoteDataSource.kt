package com.catchmate.data.datasource.remote

import com.catchmate.data.dto.auth.UserDto
import com.google.android.gms.tasks.Task

interface UserRemoteDataSource {
    suspend fun saveUser(uid: String, userDto: UserDto)
}
