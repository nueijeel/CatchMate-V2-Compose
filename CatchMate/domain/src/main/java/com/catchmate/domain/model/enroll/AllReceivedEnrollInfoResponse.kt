package com.catchmate.domain.model.enroll

data class AllReceivedEnrollInfoResponse(
    val boardInfo: EnrollBoardInfo,
    val enrollReceiveInfoList: List<ReceivedEnrollInfo>,
)
