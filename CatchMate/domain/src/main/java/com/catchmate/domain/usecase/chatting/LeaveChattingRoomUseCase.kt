package com.catchmate.domain.usecase.chatting

import com.catchmate.domain.model.chatting.DeleteChattingRoomResponse
import com.catchmate.domain.repository.ChattingRepository
import javax.inject.Inject

class LeaveChattingRoomUseCase
    @Inject
    constructor(
        private val chattingRepository: ChattingRepository,
    ) {
        suspend operator fun invoke(chatRoomId: Long): Result<DeleteChattingRoomResponse> =
            chattingRepository.deleteChattingRoom(chatRoomId)
    }
