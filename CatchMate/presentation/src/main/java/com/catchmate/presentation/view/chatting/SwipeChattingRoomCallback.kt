package com.catchmate.presentation.view.chatting

import android.graphics.Canvas
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.catchmate.presentation.R
import com.catchmate.presentation.view.notification.SwipeBackgroundHelper

class SwipeChattingRoomCallback(
    private val recyclerView: RecyclerView,
) : ItemTouchHelper.Callback() {
    var adapter: ChattingRoomListAdapter = (recyclerView.adapter as ChattingRoomListAdapter)

    // dragFlags 0으로 지정해서 드래그 비허용, swipeFlags LEFT로 지정해서 왼쪽 스와이프 허용
    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
    ): Int = makeMovementFlags(0, ItemTouchHelper.LEFT)

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder,
    ): Boolean = false

    override fun onSwiped(
        viewHolder: RecyclerView.ViewHolder,
        direction: Int,
    ) {
        // swipe 발생시킨 아이템 position
        val deletedPos = viewHolder.absoluteAdapterPosition
        // 안전하게 접근
        if (deletedPos != RecyclerView.NO_POSITION && deletedPos < adapter.currentList.size) {
            val deletedItemId = adapter.currentList[deletedPos].chatRoomId

            if (direction == ItemTouchHelper.LEFT) {
                // adapter에서 아이템 제거
                adapter.swipeItem(deletedPos, deletedItemId)
            }
        }
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean,
    ) {
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            val viewItem = viewHolder.itemView
            if (dX < 0) {
                SwipeBackgroundHelper.paintDrawCommandToStart(
                    c,
                    viewItem,
                    R.color.brand500,
                    dX,
                )
            }
        }
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }
}
