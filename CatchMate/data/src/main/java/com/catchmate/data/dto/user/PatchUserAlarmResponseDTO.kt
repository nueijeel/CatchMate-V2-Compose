package com.catchmate.data.dto.user

import com.catchmate.domain.model.enumclass.AlarmType

data class PatchUserAlarmResponseDTO(
    val userId: Long,
    val alarmType: AlarmType,
    val isEnabled: String,
    val createdAt: String,
)
