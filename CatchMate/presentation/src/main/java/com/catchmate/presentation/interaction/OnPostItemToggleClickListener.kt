package com.catchmate.presentation.interaction

interface OnPostItemToggleClickListener {
    fun onPostItemToggleClicked(
        boardId: Long,
        position: Int,
    )
}
