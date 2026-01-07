package com.catchmate.presentation.interaction

interface OnChattingRoomSelectedListener {
    fun onChattingRoomSelected(
        chatRoomId: Long,
        isNewChatRoom: Boolean,
    )
}
