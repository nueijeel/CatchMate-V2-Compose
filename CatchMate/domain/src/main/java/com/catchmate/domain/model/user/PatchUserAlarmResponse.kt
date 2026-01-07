package com.catchmate.domain.model.user

import com.catchmate.domain.model.enumclass.AlarmType

data class PatchUserAlarmResponse(
    val userId: Long,
    val alarmType: AlarmType,
    val isEnabled: String,
    val createdAt: String,
)
