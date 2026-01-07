package com.catchmate.presentation.interaction

interface OnBlockedUserSelectedListener {
    fun onBlockedUserSelected(
        pos: Int,
        userId: Long,
        nickname: String,
    )
}
