package com.catchmate.data.datasource.remote

import com.catchmate.data.dto.auth.UserDto

interface UserRemoteDataSource {
    suspend fun saveUser(uid: String, userDto: UserDto)
}
