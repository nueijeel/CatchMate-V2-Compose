package com.catchmate.domain.usecase.chatting

import com.catchmate.domain.model.chatting.DeleteChattingCrewKickOutResponse
import com.catchmate.domain.repository.ChattingRepository
import javax.inject.Inject

class KickOutChattingCrewUseCase
    @Inject
    constructor(
        private val chattingRepository: ChattingRepository,
    ) {
        suspend operator fun invoke(
            chatRoomId: Long,
            userId: Long,
        ): Result<DeleteChattingCrewKickOutResponse> = chattingRepository.deleteChattingCrewKickOut(chatRoomId, userId)
    }
