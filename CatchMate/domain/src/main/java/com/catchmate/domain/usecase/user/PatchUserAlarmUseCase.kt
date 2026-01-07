package com.catchmate.domain.usecase.user

import com.catchmate.domain.model.user.PatchUserAlarmResponse
import com.catchmate.domain.repository.UserRepository
import javax.inject.Inject

class PatchUserAlarmUseCase
    @Inject
    constructor(
        private val userRepository: UserRepository,
    ) {
        suspend fun patchUserAlarm(
            alarmType: String,
            isEnabled: String,
        ): Result<PatchUserAlarmResponse> = userRepository.patchUserAlarm(alarmType, isEnabled)
    }
