package com.catchmate.domain.usecase.chatting

import com.catchmate.domain.model.chatting.ChatRoomInfo
import com.catchmate.domain.repository.ChattingRepository
import javax.inject.Inject

class GetChattingRoomInfoUseCase
    @Inject
    constructor(
        private val chattingRepository: ChattingRepository,
    ) {
        suspend operator fun invoke(chatRoomId: Long): Result<ChatRoomInfo> = chattingRepository.getChattingRoomInfo(chatRoomId)
    }
