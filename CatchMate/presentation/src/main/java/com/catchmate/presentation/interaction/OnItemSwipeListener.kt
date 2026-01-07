package com.catchmate.presentation.interaction

interface OnItemSwipeListener {
    fun onNotificationItemSwipe(
        position: Int,
        swipedItemId: Long,
    )
}
