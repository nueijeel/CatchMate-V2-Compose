package com.catchmate.domain.usecase.chatting

import com.catchmate.domain.model.chatting.GetChattingRoomListResponse
import com.catchmate.domain.repository.ChattingRepository
import javax.inject.Inject

class GetChattingRoomListUseCase
    @Inject
    constructor(
        private val chattingRepository: ChattingRepository,
    ) {
        suspend fun getChattingRoomList(page: Int): Result<GetChattingRoomListResponse> = chattingRepository.getChattingRoomList(page)
    }
