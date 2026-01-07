package com.catchmate.data.dto.enroll

data class AllReceivedEnrollInfoResponseDTO(
    val boardInfo: EnrollBoardInfoDTO,
    val enrollReceiveInfoList: List<ReceivedEnrollInfoDTO>,
)
