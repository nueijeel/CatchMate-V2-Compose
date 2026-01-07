package com.catchmate.domain.usecase.chatting

import com.catchmate.domain.model.chatting.PutChattingRoomAlarmResponse
import com.catchmate.domain.repository.ChattingRepository
import javax.inject.Inject

class PutChattingRoomAlarmUseCase
    @Inject
    constructor(
        private val chattingRepository: ChattingRepository,
    ) {
        suspend operator fun invoke(
            chatRoomId: Long,
            enable: Boolean,
        ): Result<PutChattingRoomAlarmResponse> = chattingRepository.putChattingRoomAlarm(chatRoomId, enable)
    }
