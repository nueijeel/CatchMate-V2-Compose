package com.catchmate.domain.usecase.chatting

import com.catchmate.domain.model.chatting.GetChattingCrewListResponse
import com.catchmate.domain.repository.ChattingRepository
import javax.inject.Inject

class GetChattingCrewListUseCase
    @Inject
    constructor(
        private val chattingRepository: ChattingRepository,
    ) {
        suspend operator fun invoke(chatRoomId: Long): Result<GetChattingCrewListResponse> =
            chattingRepository.getChattingCrewList(chatRoomId)
    }
